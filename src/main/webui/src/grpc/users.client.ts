// @generated by protobuf-ts 2.9.4 with parameter optimize_code_size
// @generated from protobuf file "users.proto" (package "users", syntax proto3)
// tslint:disable
import type { RpcTransport } from "@protobuf-ts/runtime-rpc";
import type { ServiceInfo } from "@protobuf-ts/runtime-rpc";
import { UsersService } from "./users";
import type { UserChange } from "./users";
import type { UserSubscription } from "./users";
import type { ServerStreamingCall } from "@protobuf-ts/runtime-rpc";
import { stackIntercept } from "@protobuf-ts/runtime-rpc";
import type { UsersList } from "./users";
import type { UserName } from "./users";
import type { UnaryCall } from "@protobuf-ts/runtime-rpc";
import type { RpcOptions } from "@protobuf-ts/runtime-rpc";
/**
 * @generated from protobuf service users.UsersService
 */
export interface IUsersServiceClient {
    /**
     * Unary sending to as a client to join the project with a name and get back a list of existing users (including itself)
     *
     * @generated from protobuf rpc: Join(users.UserName) returns (users.UsersList);
     */
    join(input: UserName, options?: RpcOptions): UnaryCall<UserName, UsersList>;
    /**
     * Server-side streaming to broadcast users joining or leaving
     *
     * @generated from protobuf rpc: GetUsersEvents(users.UserSubscription) returns (stream users.UserChange);
     */
    getUsersEvents(input: UserSubscription, options?: RpcOptions): ServerStreamingCall<UserSubscription, UserChange>;
}
/**
 * @generated from protobuf service users.UsersService
 */
export class UsersServiceClient implements IUsersServiceClient, ServiceInfo {
    typeName = UsersService.typeName;
    methods = UsersService.methods;
    options = UsersService.options;
    constructor(private readonly _transport: RpcTransport) {
    }
    /**
     * Unary sending to as a client to join the project with a name and get back a list of existing users (including itself)
     *
     * @generated from protobuf rpc: Join(users.UserName) returns (users.UsersList);
     */
    join(input: UserName, options?: RpcOptions): UnaryCall<UserName, UsersList> {
        const method = this.methods[0], opt = this._transport.mergeOptions(options);
        return stackIntercept<UserName, UsersList>("unary", this._transport, method, opt, input);
    }
    /**
     * Server-side streaming to broadcast users joining or leaving
     *
     * @generated from protobuf rpc: GetUsersEvents(users.UserSubscription) returns (stream users.UserChange);
     */
    getUsersEvents(input: UserSubscription, options?: RpcOptions): ServerStreamingCall<UserSubscription, UserChange> {
        const method = this.methods[1], opt = this._transport.mergeOptions(options);
        return stackIntercept<UserSubscription, UserChange>("serverStreaming", this._transport, method, opt, input);
    }
}
