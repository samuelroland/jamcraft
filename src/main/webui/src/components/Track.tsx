'use client'

import { useState, useEffect, useRef } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Slider } from "@/components/ui/slider"
import { Button } from "@/components/ui/button"
import { Play, Pause } from 'lucide-react'

interface TrackProps {
    id: number
    name: string
    duration: number // Duration in seconds
}
 function Track({ duration }: TrackProps) {
    const [currentTime, setCurrentTime] = useState(0)
    const [isPlaying, setIsPlaying] = useState(false)
    const requestRef = useRef<number>()
    const previousTimeRef = useRef<number>()

    const formatTime = (time: number) => {
        const minutes = Math.floor(time / 60)
        const seconds = Math.floor(time % 60)
        return `${minutes}:${seconds.toString().padStart(2, '0')}`
    }

    const animate = (time: number) => {
        if (previousTimeRef.current != undefined) {
            const deltaTime = time - previousTimeRef.current
            if (isPlaying) {
                setCurrentTime((prevTime) =>
                    prevTime + deltaTime / 1000 > duration ? 0 : prevTime + deltaTime / 1000
                )
            }
        }
        previousTimeRef.current = time
        requestRef.current = requestAnimationFrame(animate)
    }

    useEffect(() => {
        requestRef.current = requestAnimationFrame(animate)
        return () => cancelAnimationFrame(requestRef.current!)
    }, [isPlaying])

    const handlePlayPause = () => {
        setIsPlaying(!isPlaying)
    }

    const handleSliderChange = (newValue: number[]) => {
        setCurrentTime(newValue[0])
    }

    return (
        <Card className="w-full max-w-4xl mx-auto">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-xl font-bold">Track Timeline</CardTitle>
                <div className="text-sm font-mono">
                    {formatTime(currentTime)} / {formatTime(duration)}
                </div>
            </CardHeader>
            <CardContent>
                <div className="relative h-32 bg-secondary rounded-md overflow-hidden mb-4">
                    {/* Time markers */}
                    {Array.from({ length: Math.ceil(duration) }, (_, i) => (
                        <div
                            key={i}
                            className={`absolute top-6 bottom-0 w-px ${i % 5 === 0 ? 'bg-primary/30' : 'bg-primary/10'}`}
                            style={{ left: `${(i / duration) * 100}%` }}
                        >
                            {i % 5 === 0 && (
                                <div className="absolute top-0 left-0 transform -translate-x-1/2 text-xs text-primary/70 mb-1">
                                    {formatTime(i)}
                                </div>
                            )}
                        </div>
                    ))}
                    {/* Playhead */}
                    <div
                        className="absolute top-6 bottom-0 w-px bg-primary z-10"
                        style={{ left: `${(currentTime / duration) * 100}%` }}
                    />
                </div>
                <div className="flex items-center space-x-4">
                    <Button
                        onClick={handlePlayPause}
                        aria-label={isPlaying ? 'Pause' : 'Play'}
                        variant="outline"
                        size="icon"
                    >
                        {isPlaying ? <Pause className="h-4 w-4" /> : <Play className="h-4 w-4" />}
                    </Button>
                    <Slider
                        value={[currentTime]}
                        max={duration}
                        step={0.1}
                        onValueChange={handleSliderChange}
                        className="flex-grow"
                        aria-label="Track progress"
                    />
                </div>
            </CardContent>
        </Card>
    )
}
export default Track
