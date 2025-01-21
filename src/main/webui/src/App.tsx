import { useEffect, useRef, useState, useMemo } from 'react';
import './App.css';
import { MouseServiceClient } from './grpc/mouse.client';
import { MousePosition } from './grpc/mouse';
import { UsersServiceClient } from './grpc/users.client.ts';
import { SessionAction, UserChange } from './grpc/users.ts';
import TrackList from './components/TrackList';
import { MIN_MOUSE_MSG_INTERVAL } from './constants';
import Library from './components/Library';
import { LoginDialog } from './components/LoginDialog.tsx';
import MouseCursor from './components/MouseCursor.tsx';
import { User } from '../types.ts';
import { Toaster } from '@/components/ui/sonner';
import { toast } from 'sonner';
import { getGrpcTransport } from './lib/utils.ts';

function App() {
    const transport = getGrpcTransport();
    const mouseClient = useMemo(() => new MouseServiceClient(transport), [transport]);
    const userClient = useMemo(() => new UsersServiceClient(transport), [transport]);

    // A way to abort requests on component destroy - useful for HMR
    const controller = new AbortController();
    const { signal } = controller;

    const [otherMouses, setOtherMouses] = useState<Map<number, MousePosition>>(new Map());
    const [users, setUsers] = useState<Map<number, string>>(new Map());

    const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
    const [selfId] = useState<number>(0);
    const selfIdRef = useRef(selfId);
    const [isLogged] = useState(false);
    const isLoggedRef = useRef(isLogged);
    // With the help of Copilot, I found that this copy is absolutely necessary
    // If we remove it, setInterval code will not be called when we continuously move the mouse
    const latestMousePosition = useRef(mousePosition);
    const previousMousePosition = useRef(mousePosition);

    // Just save the new position each time it moves
    const onMouseMove = (event: MouseEvent) => {
        const newPosition = { x: event.clientX, y: event.clientY };
        setMousePosition(newPosition);
        latestMousePosition.current = newPosition; // this copy is important
    };

    const handleLogout = () => {
        // console.log('event fired');
        // const promise = userClient.leave({ userId: selfIdRef.current });
        // promise.catch((error) => {
        //     console.log(error);
        // });
    };

    const handleLogin = (username: string) => {
        const promise = userClient.join({ name: username }, { timeout: 2000, abort: signal });
        promise
            .then((call) => {
                for (const user of call.response.users) {
                    if (user.name == username) {
                        selfIdRef.current = user.id;
                        localStorage.setItem('user', JSON.stringify(user));
                    }
                    setUsers((u) => {
                        // Create a new Map to maintain immutability
                        const newMap = new Map(u);
                        newMap.set(user.id, user.name);
                        return newMap;
                    });
                }
                isLoggedRef.current = true;
                toast.success('Successfully logged as ' + username);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    const handleUpdatedMousesPositions = (p: MousePosition) => {
        // console.log('Got new position', p);
        if (p.userId == selfIdRef.current) {
            return;
        }
        setOtherMouses((ms) => {
            // Create a new Map to maintain immutability
            const newMap = new Map(ms);
            newMap.set(p.userId, p);
            return newMap;
        });
    };
    const handleUserEvent = (uc: UserChange) => {
        console.log('uc', uc);
        if (uc.action == SessionAction.JOIN) {
            setUsers((users) => {
                const newMap = new Map(users);
                newMap.set(uc.userId, uc.name);
                return newMap;
            });
            toast.info(uc.name + ' joined');
        }
        if (uc.action == SessionAction.LEAVE) {
            setUsers((users) => {
                const newMap = new Map(users);
                newMap.delete(uc.userId);
                return newMap;
            });
            setOtherMouses((ms) => {
                const newMap = new Map(ms);
                newMap.delete(uc.userId);
                return newMap;
            });
            toast.info(uc.name + ' disconnected');
        }
    };

    useEffect(() => {
        mouseClient.getMouseUpdates({}, { timeout: 10000000, abort: signal }).responses.onMessage((p) => handleUpdatedMousesPositions(p));
        userClient.getUsersEvents({}, { timeout: 10000000, abort: signal }).responses.onMessage((uc) => handleUserEvent(uc));
        addEventListener('mousemove', onMouseMove);
        addEventListener('beforeunload', handleLogout);
        // Try to load user persisted in local storage
        const user = localStorage.getItem('user');
        if (user) {
            const u = JSON.parse(user) as User;
            handleLogin(u.name);
        }

        // Each MIN_MOUSE_MSG, if the position has changed, send the new one
        const intervalId = setInterval(() => {
            if (!isLoggedRef.current) return;
            const { x: currentX, y: currentY } = latestMousePosition.current;
            const { x: previousX, y: previousY } = previousMousePosition.current;

            if (currentX !== previousX || currentY !== previousY) {
                previousMousePosition.current = { x: currentX, y: currentY };
                mouseClient.sendMousePosition({ userId: selfIdRef.current, x: currentX, y: currentY }, { timeout: 100, abort: signal });
            }
        }, MIN_MOUSE_MSG_INTERVAL);

        return () => {
            controller.abort();
            clearInterval(intervalId);
            removeEventListener('mousemove', onMouseMove);
        };
    }, []); // run this only once, not on every render

    return (
        <>
            <Toaster position="top-right" richColors expand={true} />
            {localStorage.getItem('user') == undefined ? <LoginDialog is_logged={isLoggedRef.current} login={handleLogin}></LoginDialog> : null}
            <div className="flex h-screen w-full overflow-hidden">
                {/* Library on the left */}
                <div className="min-w-80 h-full bg-gray-100 p-4 relative">
                    <Library />
                    {selfIdRef.current != 0 ? (
                        <div>
                            Logged as <span className="font-bold">{users.get(selfIdRef.current)}</span>
                        </div>
                    ) : null}
                </div>

                {/* TrackList on the right */}
                <div className="ml-1/4 p-2 w-full max-w-screen">
                    <TrackList />
                </div>
            </div>
            {Array.from(otherMouses.values()).map((p) => (
                <MouseCursor key={p.userId} p={p} username={users.get(p.userId) ?? '?'}></MouseCursor>
            ))}
        </>
    );
}

export default App;
