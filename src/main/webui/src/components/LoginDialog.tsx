import { Button } from './ui/button';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from './ui/dialog';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { useState } from 'react';

export function LoginDialog({ is_logged, login }) {
    const [username, setUsername] = useState('');

    const handleChange = (e) => {
        setUsername(e.target.value);
    };

    return (
        <Dialog open={!is_logged}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>Welcome to Jamcraft</DialogTitle>
                    <DialogDescription>Please provide a username to access the app</DialogDescription>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                    <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="username" className="text-right">
                            Username
                        </Label>
                        <Input id="username" className="col-span-3" onChange={(e) => handleChange(e)} />
                    </div>
                </div>
                <DialogFooter>
                    <Button onClick={() => login(username)}>Login</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
