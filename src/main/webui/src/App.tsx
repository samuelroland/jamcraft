import { useEffect, useRef, useState ,useMemo} from 'react'
import './App.css'
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport'
import { MouseServiceClient } from './grpc/mouse.client'
import { MousePosition } from './grpc/mouse'
import MouseCursor from './components/MouseCursor'
import TrackList from './components/TrackList'
import { MIN_MOUSE_MSG_INTERVAL, PROXY_BASE_URL } from './constants'

function App() {
 const transport = useMemo(() => new GrpcWebFetchTransport({
    baseUrl: PROXY_BASE_URL,
    format: 'binary',
  }), [])

  const mouseClient = useMemo(() => new MouseServiceClient(transport), [transport])


  const [userId] = useState(Math.floor(Math.random() * 1000)) // random one for now
  const [otherMouses, setOtherMouses] = useState<Map<number, MousePosition>>(new Map())

  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 })
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

  const handleUpdatedMousesPositions = (p: MousePosition) => {
    console.log('Got new position', p)
    if(p.userId == userId){
        return
    }
     setOtherMouses((ms) => {
        // Create a new Map to maintain immutability
        const newMap = new Map(ms)
        newMap.set(p.userId, p)
        return newMap
      })

  }

  useEffect(() => {
    mouseClient.getMouseUpdates({}).responses.onMessage((p) => handleUpdatedMousesPositions(p))

    addEventListener('mousemove', onMouseMove)

    // Each MIN_MOUSE_MSG, if the position has changed, send the new one
    const intervalId = setInterval(() => {
      const { x: currentX, y: currentY } = latestMousePosition.current
      const { x: previousX, y: previousY } = previousMousePosition.current

      if (currentX !== previousX || currentY !== previousY) {
        previousMousePosition.current = { x: currentX, y: currentY }
        console.log('Sent mouse position at ', latestMousePosition.current)
        mouseClient.sendMousePosition({ userId: userId, x: currentX, y: currentY })
      }
    }, MIN_MOUSE_MSG_INTERVAL)

    return () => {
      clearInterval(intervalId)
      removeEventListener('mousemove', onMouseMove)
    }
  }, []) // run this only once, not on every render

  return (
    <>
      <h1>POC mouses</h1>
      <h3>Client ID {userId}</h3>
      Mouse position {latestMousePosition.current.x} {latestMousePosition.current.y} <br />
      Sent position {previousMousePosition.current.x} {previousMousePosition.current.y}
      <br />
      <h2>Stored mouse positions</h2>
      <ul>
         {Array.from(otherMouses.values()).map((p) => (
          <li key={p.userId}>
            userId: {p.userId}, x: {p.x}, y: {p.y}
          </li>
        ))}
      </ul>
      <ul>
        {Array.from(otherMouses.values()).map((p) => (
          <MouseCursor key={p.userId} p={p}></MouseCursor>
        ))}
      </ul>
        <TrackList></TrackList>
    </>
  )
}

export default App
