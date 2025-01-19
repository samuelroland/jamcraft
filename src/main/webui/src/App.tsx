import { useEffect, useRef, useState, useMemo } from 'react'
import './App.css'
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport'
import { MouseServiceClient } from './grpc/mouse.client'
import { MousePosition } from './grpc/mouse'
import { UsersServiceClient} from "./grpc/users.client.ts";
import {SessionAction} from "./grpc/users.ts"
import TrackList from './components/TrackList'
import { MIN_MOUSE_MSG_INTERVAL, PROXY_BASE_URL } from './constants'
import Library from './components/Library'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {LoginDialog} from "./components/LoginDialog.tsx";
import MouseCursor   from "./components/MouseCursor.tsx";

function App() {
    const transport = useMemo(() => new GrpcWebFetchTransport({
        baseUrl: PROXY_BASE_URL,
        format: 'binary',
    }), [])

    const mouseClient = useMemo(() => new MouseServiceClient(transport), [transport])
    const userClient = useMemo( ()=> new UsersServiceClient(transport),[transport])

    // @ts-ignore
    const [otherMouses, setOtherMouses] = useState<Map<number, MousePosition>>(new Map())
    const [users,setUsers] = useState<Map<number,String>>(new Map())

    const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 })
    const [selfId,setSelfId] = useState<number>(10)
    const selfIdRef = useRef(selfId)
    const [isLogged,setIsLogged] = useState(false)
    const isLoggedRef = useRef(isLogged)
    // With the help of Copilot, I found that this copy is absolutely necessary
    // If we remove it, setInterval code will not be called when we continuously move the mouse
    const latestMousePosition = useRef(mousePosition)
    const previousMousePosition = useRef(mousePosition)

    // Just save the new position each time it moves
    const onMouseMove = (event: MouseEvent) => {
        const newPosition = { x: event.clientX, y: event.clientY }
        setMousePosition(newPosition)
        latestMousePosition.current = newPosition // this copy is important
    }
    const handleLogin = (username) =>{
        const promise = userClient.join({name:username})
        promise.then((call)=> {
            console.log(call.response.users)
            for(let user of call.response.users ) {
                console.log(username,user.name)
                console.log(user.name == username)
                if (user.name == username) {
                    selfIdRef.current = Number(user.id)
                }
                setUsers((u) => {
                    // Create a new Map to maintain immutability
                    const newMap = new Map(u)
                    newMap.set(Number(user.id), user.name)
                    return newMap
                })
            }
            isLoggedRef.current = true
        }).catch(error =>{
            console.log(error)
        })
    }

    const handleUpdatedMousesPositions = (p: MousePosition) => {
        console.log('Got new position', p)
        if (p.userId == selfIdRef.current) {
            return
        }
        setOtherMouses((ms) => {
            // Create a new Map to maintain immutability
            const newMap = new Map(ms)
            newMap.set(p.userId, p)
            return newMap
        })

    }
    const handleUserEvent = (uc) =>{
        if(uc.action == SessionAction.JOIN){
            setUsers(users => {
                const newMap = new Map(users)
                newMap.set(Number(uc.userId), uc.name)
                return newMap
            })
        }
        if(uc.action == SessionAction.LEAVE){
            console.log("User left")
        }
    }
    useEffect(()=>{
        console.log("users changed",users)
        console.log("othermouse",otherMouses)
    },[users])
    useEffect(() => {
        mouseClient.getMouseUpdates({}).responses.onMessage((p) => handleUpdatedMousesPositions(p))
        userClient.getUsersEvents({}).responses.onMessage(uc => handleUserEvent(uc))
        addEventListener('mousemove', onMouseMove)

        // Each MIN_MOUSE_MSG, if the position has changed, send the new one
        const intervalId = setInterval(() => {
            if(!isLoggedRef.current) return;
            const { x: currentX, y: currentY } = latestMousePosition.current
            const { x: previousX, y: previousY } = previousMousePosition.current

            if (currentX !== previousX || currentY !== previousY) {
                previousMousePosition.current = { x: currentX, y: currentY }
                mouseClient.sendMousePosition({ userId: Number(selfIdRef.current), x: currentX, y: currentY })
            }
        }, MIN_MOUSE_MSG_INTERVAL)

        return () => {
            clearInterval(intervalId)
            removeEventListener('mousemove', onMouseMove)
        }
    }, []) // run this only once, not on every render

    return (
        <>
            <LoginDialog is_logged={isLoggedRef.current} login={handleLogin}></LoginDialog>
            <ToastContainer position="top-right" autoClose={4000} hideProgressBar={true} pauseOnHover={true}></ToastContainer>
            <div className="flex h-screen">
                {/* Library on the left */}
                <div className="w-1/4 h-full bg-gray-100 p-4 relative">
                    <Library/>
                </div>

                {/* TrackList on the right */}
                <div className="ml-1/4 p-6 w-full">
                    <TrackList/>
                </div>
            </div>
            {Array.from(otherMouses.values()).map((p) => (
                <MouseCursor key={p.userId} p={p} username={users.get(p.userId)}></MouseCursor>
            ))}
        </>
    )
}

export default App
