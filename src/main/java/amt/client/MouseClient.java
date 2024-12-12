package amt.client;

import amt.MousePosition;
import amt.MouseServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;

public class MouseClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();

        MouseServiceGrpc.MouseServiceStub stub = MouseServiceGrpc.newStub(channel);

        StreamObserver<MousePosition> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(MousePosition mousePosition) {
                System.out.println("Received position: User " + mousePosition.getUserId()
                        + " at (" + mousePosition.getX() + ", " + mousePosition.getY() + ")");
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed.");
            }
        };

        StreamObserver<MousePosition> requestObserver = stub.realTimeMouseSync(responseObserver);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter your user ID:");
            int userId = scanner.nextInt();

            while (true) {
                System.out.println("Enter x and y coordinates (or -1 -1 to exit):");
                int x = scanner.nextInt();
                int y = scanner.nextInt();

                if (x == -1 && y == -1) {
                    break;
                }

                MousePosition position = MousePosition.newBuilder()
                        .setUserId(userId)
                        .setX(x)
                        .setY(y)
                        .build();
                requestObserver.onNext(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            requestObserver.onCompleted();
            channel.shutdown();
        }
    }
}

