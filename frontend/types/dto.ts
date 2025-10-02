// API 요청/응답을 위한 DTO 타입들 (Zod 스키마에서 파생)

import {
  ActorSchema,
  AuthResponseSchema,
  BARTFirstRoundClickRequestSchema,
  BARTResponseSchema,
  BARTResultSchema,
  BARTRoundFinishRequestSchema,
  BARTRoundRequestSchema,
  BigFiveCodeSchema,
  ChatMessageSchema,
  ChatQueryOptionsSchema,
  CompleteSurveyRequestSchema,
  CreateDiaryRequestSchema,
  CreateGameSessionRequestSchema,
  CreateInviteResponseSchema,
  DiaryAnalysisSchema,
  DiaryCalendarResponseSchema,
  DiaryChatMessageSchema,
  DiaryChatRequestSchema,
  DiaryChatResponseSchema,
  DiaryQueryOptionsSchema,
  DiarySchema,
  FriendChatMessageSchema,
  FriendInvitationFullSchema,
  FriendInvitationSchema,
  FriendResponseSchema,
  FriendSchema,
  GNGClickRequestSchema,
  GNGResponseSchema,
  GNGResultSchema,
  GameResultSchema,
  GameSessionIdSchema,
  GameSessionSchema,
  LoginRequestSchema,
  MarkAsReadRequestSchema,
  MyChatMessageSchema,
  NotificationCountResponseSchema,
  NotificationQueryOptionsSchema,
  NotificationSchema,
  PaginationParamsSchema,
  ReportResponseSchema,
  ReportSchema,
  SendMessageRequestSchema,
  SurveyQuestionSchema,
  SurveyRequestSchema,
  SurveyResponseLogRequestSchema,
  SurveyResponseLogSchema,
  SurveyResponseSchema,
  SurveySchema,
  TokenReissueResponseSchema,
  UGOrderSchema,
  UGResponseRequestSchema,
  UGResponseSchema,
  UGResultSchema,
  UGRoundRequestSchema,
  UpdateDiaryRequestSchema,
  UpdateUserRequestSchema,
  UserPersonaResponseSchema,
  UserPersonaSchema,
  UserSchema
} from "./schema.zod"

import { z } from "zod"

// Auth DTOs
export type LoginRequestDTO = z.infer<typeof LoginRequestSchema>
export type AuthResponseDTO = z.infer<typeof AuthResponseSchema>
export type GoogleAuthResponseDTO = {
  loginUrl: string
}
export type TokenReissueResponseDTO = z.infer<typeof TokenReissueResponseSchema>

// User DTOs
export type UserDTO = z.infer<typeof UserSchema>
export type UpdateUserRequestDTO = z.infer<typeof UpdateUserRequestSchema>
export type UserPersonaDTO = z.infer<typeof UserPersonaSchema>
export type UserPersonaResponseDTO = z.infer<typeof UserPersonaResponseSchema>

// Game DTOs
export type GameSessionDTO = z.infer<typeof GameSessionSchema>
export type GameSessionIdDTO = z.infer<typeof GameSessionIdSchema>
export type CreateGameSessionRequestDTO = z.infer<typeof CreateGameSessionRequestSchema>
export type GameResultDTO = z.infer<typeof GameResultSchema>

// UG Game DTOs
export type UGOrderDTO = z.infer<typeof UGOrderSchema>
export type UGResponseRequestDTO = z.infer<typeof UGResponseRequestSchema>
export type UGResponseDTO = z.infer<typeof UGResponseSchema>
export type UGResultDTO = z.infer<typeof UGResultSchema>
export type UGRoundRequestDTO = z.infer<typeof UGRoundRequestSchema>

// BART Game DTOs
export type BARTFirstRoundClickRequestDTO = z.infer<typeof BARTFirstRoundClickRequestSchema>
export type BARTRoundRequestDTO = z.infer<typeof BARTRoundRequestSchema>
export type BARTRoundFinishRequestDTO = z.infer<typeof BARTRoundFinishRequestSchema>
export type BARTResponseDTO = z.infer<typeof BARTResponseSchema>
export type BARTResultDTO = z.infer<typeof BARTResultSchema>

// GNG Game DTOs
export type GNGClickRequestDTO = z.infer<typeof GNGClickRequestSchema>
export type GNGResponseDTO = z.infer<typeof GNGResponseSchema>
export type GNGResultDTO = z.infer<typeof GNGResultSchema>

// Diary DTOs (matching OpenAPI schemas)
export type DiaryDTO = z.infer<typeof DiarySchema>
export type DiaryResponseDTO = DiaryDTO // OpenAPI DiaryResponse와 동일
export type CreateDiaryRequestDTO = z.infer<typeof CreateDiaryRequestSchema>
export type UpdateDiaryRequestDTO = z.infer<typeof UpdateDiaryRequestSchema>
export type DiaryCalendarResponseDTO = z.infer<typeof DiaryCalendarResponseSchema>

// Diary Chat DTOs (matching OpenAPI schemas)
export type DiaryChatRequestDTO = z.infer<typeof DiaryChatRequestSchema>
export type DiaryChatResponseDTO = z.infer<typeof DiaryChatResponseSchema>
export type SendDiaryChatMessageRequestDTO = DiaryChatRequestDTO

// Legacy Diary Chat Message DTO (keep for backward compatibility)
export type DiaryChatMessageDTO = z.infer<typeof DiaryChatMessageSchema>

// DiaryAnalysis DTOs (updated to match backend DiaryAnalysisSummary entity)
export type DiaryAnalysisDTO = z.infer<typeof DiaryAnalysisSchema>

// Friend DTOs
export type FriendDTO = z.infer<typeof FriendSchema>
export type FriendResponseDTO = z.infer<typeof FriendResponseSchema>
export type FriendInvitationDTO = z.infer<typeof FriendInvitationSchema>
export type FriendInvitationFullDTO = z.infer<typeof FriendInvitationFullSchema>
export type CreateInviteResponseDTO = z.infer<typeof CreateInviteResponseSchema>

// Chat DTOs
export type ChatMessageDTO = z.infer<typeof ChatMessageSchema>
export type SendMessageRequestDTO = z.infer<typeof SendMessageRequestSchema>
export type MyChatMessageDTO = z.infer<typeof MyChatMessageSchema>
export type FriendChatMessageDTO = z.infer<typeof FriendChatMessageSchema>

// Actor DTOs
export type ActorDTO = z.infer<typeof ActorSchema>

// Big Five Codes DTOs
export type BigFiveCodeDTO = z.infer<typeof BigFiveCodeSchema>

// Survey DTOs
export type SurveyDTO = z.infer<typeof SurveySchema>
export type SurveyRequestDTO = z.infer<typeof SurveyRequestSchema>
export type SurveyQuestionDTO = z.infer<typeof SurveyQuestionSchema>
export type SurveyResponseDTO = z.infer<typeof SurveyResponseSchema>
export type SurveyResponseLogRequestDTO = z.infer<typeof SurveyResponseLogRequestSchema>
export type SurveyResponseLogDTO = z.infer<typeof SurveyResponseLogSchema>
export type CompleteSurveyRequestDTO = z.infer<typeof CompleteSurveyRequestSchema>

// Report DTOs
export type ReportDTO = z.infer<typeof ReportSchema>
export type ReportResponseDTO = z.infer<typeof ReportResponseSchema>

// Notification DTOs
export type NotificationDTO = z.infer<typeof NotificationSchema>
export type MarkAsReadRequestDTO = z.infer<typeof MarkAsReadRequestSchema>
export type NotificationCountResponseDTO = z.infer<typeof NotificationCountResponseSchema>

// Common DTOs
export type PaginationParamsDTO = z.infer<typeof PaginationParamsSchema>
export type PaginatedResponseDTO<T> = {
  data: T[]
  total: number
  page: number
  limit: number
  totalPages: number
}
export type ApiResponseDTO<T> = {
  isSuccess: boolean
  data?: T
  error?: string
  message?: string
}

// OpenAPI Standard Response Wrapper DTOs
export type ApiResponseDiaryResponseDTO = {
  isSuccess: boolean
  code: string
  message: string
  result: DiaryResponseDTO
}

export type ApiResponseDiaryCalendarResponseDTO = {
  isSuccess: boolean
  code: string
  message: string
  result: DiaryCalendarResponseDTO
}

export type ApiResponseDiaryChatResponseDTO = {
  isSuccess: boolean
  code: string
  message: string
  result: DiaryChatResponseDTO
}

export type ApiResponsePageDiaryChatResponseDTO = {
  isSuccess: boolean
  code: string
  message: string
  result: PageDiaryChatResponseDTO
}

export type ApiResponseLongDTO = {
  isSuccess: boolean
  code: string
  message: string
  result: number // int64
}

// Pagination Support DTOs (from OpenAPI)
export type PageableObjectDTO = {
  paged: boolean
  pageNumber: number // int32
  pageSize: number // int32
  offset: number // int64
  sort: SortObjectDTO
  unpaged: boolean
}

export type SortObjectDTO = {
  sorted: boolean
  empty: boolean
  unsorted: boolean
}

export type PageDiaryChatResponseDTO = {
  totalElements: number // int64
  totalPages: number // int32
  pageable: PageableObjectDTO
  size: number // int32
  content: DiaryChatResponseDTO[]
  number: number // int32
  sort: SortObjectDTO
  numberOfElements: number // int32
  first: boolean
  last: boolean
  empty: boolean
}

// Query Options DTOs
export type DiaryQueryOptionsDTO = z.infer<typeof DiaryQueryOptionsSchema>
export type NotificationQueryOptionsDTO = z.infer<typeof NotificationQueryOptionsSchema>
export type ChatQueryOptionsDTO = z.infer<typeof ChatQueryOptionsSchema>

// Diary API Response Types (for reference)
export type GetDiaryResponse = ApiResponseDiaryResponseDTO
export type GetDiaryCalendarResponse = ApiResponseDiaryCalendarResponseDTO
export type SendDiaryChatResponse = ApiResponseDiaryChatResponseDTO
export type GetDiaryChatHistoryResponse = ApiResponsePageDiaryChatResponseDTO
export type GetDiaryChatCountResponse = ApiResponseLongDTO
