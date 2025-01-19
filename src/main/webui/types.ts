// Core types from the Rest endpoints
// Other types will come from GRPC generated code

// TODO: should we use this User type or GRPC duplication ?
export type User = {
    id: number;
    name: string;
    createdAt: Date;
};

export type Sample = {
    id: number;
    name: string;
    filepath: string;
    duration: number;
    createdAt: Date;
};

// A sample in a given track have the additionnal info of startTime
export interface SampleInTrack {
    id: number;
    sample: Sample;
    startTime: number;
    trackId: number;
    trackName: string;
}

export type Track = {
    id: number;
    name: string;
    createdAt: Date;
    modifiedAt: Date;
    samples: SampleInTrack[];
};
