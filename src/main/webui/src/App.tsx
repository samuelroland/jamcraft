import { useEffect, useRef, useState } from 'react'
import './App.css'
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport'
import { HelloGrpcClient } from './hello.client'
import { MouseServiceClient } from './mouse.client'
import Test from './Test'
const PROXY_BASE_URL = 'http://localhost:8081'
const MIN_MOUSE_MSG = 300 // 300 ms -> 3 times/second
// TODO: we should increase this number if the performance cost is acceptable

function App() {
  const transport = new GrpcWebFetchTransport({
    baseUrl: PROXY_BASE_URL,
    format: 'binary',
  })

  const mouseClient = new MouseServiceClient(transport)

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

  useEffect(() => {
    mouseClient.getMouseUpdates({}).responses.onMessage((e) => console.log('Got new mouse position', e))

    addEventListener('mousemove', onMouseMove)

    // Each MIN_MOUSE_MSG, if the position has changed, send the new one
    const intervalId = setInterval(() => {
      const { x: currentX, y: currentY } = latestMousePosition.current
      const { x: previousX, y: previousY } = previousMousePosition.current

      if (currentX !== previousX || currentY !== previousY) {
        previousMousePosition.current = { x: currentX, y: currentY }
        console.log('Sent mouse position at ', latestMousePosition.current)
        mouseClient.sendMousePosition({ userId: 12, x: currentX, y: currentY })
      }
    }, MIN_MOUSE_MSG)

    return () => {
      clearInterval(intervalId)
      removeEventListener('mousemove', onMouseMove)
    }
  }, [])

  return (
    <>
      <h1>POC mouses</h1>
      Mouse position {mousePosition.x} {mousePosition.y} <br />
      Sent position {previousMousePosition.current.x} {previousMousePosition.current.y}
    </>
  )
}

export default App
