// Core types from the Rest endpoints
// Other types will come from GRPC generated code

// TODO: should we use this User type or GRPC duplication ?
export type User = {
  id: number
  name: string
  createdAt: Date
}

export type Sample = {
  id: number
  name: string
  filepath: string
  duration: number
  createdAt: Date
}

// A sample in a given track have the additionnal info of startTime
export interface SampleInTrack extends Sample {
  startTime: number // in seconds
  instanceId: number // sample instance (sample_tracks.id not samples.id)
}

export type Track = {
  id: number
  name: string
  createdAt: Date
  modifiedAt: Date
  samples: SampleInTrack[]
}
