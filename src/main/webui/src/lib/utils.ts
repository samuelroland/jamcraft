import { GrpcWebFetchTransport } from '@protobuf-ts/grpcweb-transport';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';
import { PROXY_BASE_URL } from '../constants';

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

let transportInstance: GrpcWebFetchTransport | null = null;

export function getGrpcTransport() {
    if (!transportInstance) {
        transportInstance = new GrpcWebFetchTransport({
            baseUrl: PROXY_BASE_URL,
            format: 'binary',
            // timeout: 2000, // no default timeout, each request has to decide or use the default...
        });
    }
    return transportInstance;
}
