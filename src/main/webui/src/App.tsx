import { useEffect, useState } from 'react'
import './App.css'
import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport'
import { HelloGrpcClient } from './hello.client'
import { MouseServiceClient } from './mouse.client'
const PROXY_BASE_URL = 'http://localhost:8081'

function App() {
  const transport = new GrpcWebFetchTransport({
    baseUrl: PROXY_BASE_URL,
    format: 'binary',
  })
  const [mousePosition, setMousePosition] = useState<{ x: number; y: number } | null>(null)
  const [lastSentPosition, setLastSentPosition] = useState(JSON.parse(JSON.stringify(mousePosition)))

  const onMouseMove = (event: MouseEvent) => {
    setMousePosition({ x: event.clientX, y: event.clientY })
    console.log('mouse moved ! new mouse position: ', mousePosition)
  }

  const mouseClient = new MouseServiceClient(transport)
  useEffect(() => {
    mouseClient.getMouseUpdates({}).responses.onMessage((e) => console.log('Got new mouse position', e))

    addEventListener('mousemove', onMouseMove)

    const id = setInterval(() => {
      if (mousePosition == null) {
        console.log('is null')
        return
      }
      if (lastSentPosition.x != 0 && mousePosition.x == lastSentPosition.x && mousePosition.y == lastSentPosition.y) {
        console.log('skipped')
        return
      }
      setLastSentPosition({ x: mousePosition.x, y: mousePosition.y })
      console.log('Sent mouse position at ', mousePosition)
      mouseClient.sendMousePosition({ userId: 12, x: mousePosition.x, y: mousePosition.y })
    }, 1000)

    return () => {
      clearInterval(id)
      removeEventListener('mousemove', onMouseMove)
    }
  }, [])

  function sendMouse() {
    mouseClient.sendMousePosition({ userId: 12, x: 123, y: 234 })
  }

  async function sayHello() {
    const client = new HelloGrpcClient(transport)
    const call = await client.sayHello({
      name: 'Jarod',
    })
    const headers = call.headers
    console.log('got response headers: ', headers)

    const response = call.response
    console.log('got response message: ', response)

    const status = call.status
    console.log('got status: ', status)

    const trailers = call.trailers
    console.log('got trailers: ', trailers)
  }
  return (
    <>
      <button onClick={() => sendMouse()}>Send mouse position</button>
    </>
  )
}

export default App
