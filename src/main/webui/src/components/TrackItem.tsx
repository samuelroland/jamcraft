'use client'

import { useState, useEffect, useRef } from 'react'
// @ts-ignore
import { Card, CardContent, CardTitle } from "@/components/ui/card"
import { Track } from "../../types.ts";

function TrackItem({ samples }: Track) {
    const [isPlaying, setIsPlaying] = useState<boolean>(false)
    const [currentTime, setCurrentTime] = useState<number>(0)
    const requestRef = useRef<number>()
    const previousTimeRef = useRef<number>()

    // Map to handle multiple instances of samples
    const audioRefs = useRef<Map<number, HTMLAudioElement>>(new Map())

    // Compute the total duration of the track
    const duration = samples.reduce((max, sample) => {
        const sampleEndTime = (sample.startTime || 0) + (sample.duration || 0)
        return Math.max(max, sampleEndTime)
    }, 0)

    // @ts-ignore
    const formatTime = (time: number) => {
        const minutes = Math.floor(time / 60)
        const seconds = Math.floor(time % 60)
        return `${minutes}:${seconds.toString().padStart(2, '0')}`
    }

    const animate = (time: number) => {
        if (previousTimeRef.current != undefined) {
            const deltaTime = time - previousTimeRef.current
            if (isPlaying) {
                setCurrentTime((prevTime) => {
                    const nextTime = prevTime + deltaTime / 1000
                    if (nextTime >= duration) {
                        handleStop()
                        return 0
                    }
                    return nextTime
                })
            }
        }
        previousTimeRef.current = time
        requestRef.current = requestAnimationFrame(animate)
    }

    useEffect(() => {
        if (isPlaying)
            requestRef.current = requestAnimationFrame(animate)
        else
            cancelAnimationFrame(requestRef.current!)
    }, [isPlaying])

    // @ts-ignore
    const handlePlay = () => {
        setIsPlaying(true)

        // For each sample, play depending on the start time
        samples.forEach((sample) => {
            const audio = new Audio(sample.filepath)
            audioRefs.current.set(sample.id, audio)

            // Play the sample at the right time
            const sampleStartTime = sample.startTime || 0
            const delay = Math.max(0, sampleStartTime - currentTime)

            setTimeout(() => {
                audio.play()
            }, delay * 1000)

            const sampleEndTime = sampleStartTime + (sample.duration || 0)
            if (sampleEndTime <= duration)
                setTimeout(() => {
                    audio.pause()
                }, (sampleEndTime - currentTime) * 1000)
        })
    }

    // @ts-ignore
    const handlePause = () => {
        setIsPlaying(false)

        // Pause all samples
        audioRefs.current.forEach((audio) => {
            audio.pause()
        })
    }

    const handleStop = () => {
        setIsPlaying(false)
        setCurrentTime(0)

        // Stop and reset all samples
        audioRefs.current.forEach((audio) => {
            audio.pause()
            audio.currentTime = 0;
        })
    }

    // @ts-ignore
    const handleSliderChange = (newValue: number[]) => {
        setCurrentTime(newValue[0])

        // Update playing position for all samples
        audioRefs.current.forEach((audio) => {
            audio.currentTime = newValue[0]
        })
    }

    // // Play or pause the sample
    // const handlePlaySample = (sample: Sample) => {
    //     if (playingSample?.id === sample.id) {
    //         audioRef.current?.pause()
    //         setPlayingSample(null)
    //     } else {
    //         const audio = new Audio(sample.filepath);
    //         audioRef.current = audio
    //         audio.play().then(() => {
    //             setPlayingSample(sample)
    //         })
    //     }
    // }

    return (
        <Card className="w-full flex flex-row">
            <CardTitle className="text-xl font-bold text-left m-2 min-w-max">{name}</CardTitle>
            <CardContent>
                <div className="relative h-32 bg-secondary rounded-md overflow-hidden">
                    {/* Time markers */}
                    <div className="relative h-32 bg-secondary rounded-md overflow-hidden">
                        {samples.map((sample) => (
                            <div
                                key={sample.id}
                                className="absolute bottom-0 h-full bg-primary/20 rounded-md"
                                style={{
                                    left: `${(sample.startTime / duration) * 100}%`,
                                    width: `${((sample.duration || 0) / duration) * 100}%`,
                                }}
                            >
                              <span className="absolute bottom-0 left-1 text-xs text-primary font-semibold">
                                {sample.name}
                              </span>
                            </div>
                        ))}
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}

export default TrackItem
