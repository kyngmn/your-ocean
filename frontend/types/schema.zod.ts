import {
  ACTOR_KIND_VALUES,
  AI_STATUS_VALUES,
  BART_COLOR_VALUES,
  BIG5_INDEX_VALUES,
  BIG5_INDEX_VALUES_KO,
  GAME_TYPE_VALUES,
  GNG_STIMULUS_VALUES,
  INVITATION_STATUS_VALUES,
  MONEY_SIZE_VALUES,
  PERSONA_CODE_VALUES,
  PERSONA_TYPE_VALUES,
  REPORT_TYPE_VALUES
} from "./enums"

import { z } from "zod"

// Auth Schemas
export const LoginRequestSchema = z.object({
  code: z.string()
})

export const AuthResponseSchema = z.object({
  accessToken: z.string(),
  refreshToken: z.string(),
  user: z.object({
    id: z.number(),
    email: z.string().regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, "유효한 이메일을 입력해주세요"),
    nickname: z.string(),
    profileImageUrl: z.string().optional(),
    provider: z.string(),
    socialId: z.string(),
    aiStatus: z.enum(AI_STATUS_VALUES),
    createdAt: z.string(),
    updatedAt: z.string().optional()
  })
})

export const TokenReissueResponseSchema = z.object({
  accessToken: z.string()
})

// User Schemas
export const UserSchema = z.object({
  id: z.number(),
  email: z.string().regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, "유효한 이메일을 입력해주세요"),
  nickname: z.string(),
  profileImageUrl: z.string().optional(),
  provider: z.string(),
  socialId: z.string(),
  aiStatus: z.enum(AI_STATUS_VALUES),
  createdAt: z.string(),
  updatedAt: z.string().optional()
})

export const UpdateUserRequestSchema = z.object({
  nickname: z.string().min(1, "닉네임을 입력해주세요").optional(),
  profileImageUrl: z.instanceof(File).optional()
})

export const UserPersonaSchema = z.object({
  id: z.number(),
  userId: z.number(),
  personaCode: z.enum(PERSONA_CODE_VALUES),
  nickname: z.string(),
  createdAt: z.string(),
  updatedAt: z.string()
})

export const UserPersonaResponseSchema = z.object({
  id: z.number(),
  userId: z.number(),
  userO: z.number(),
  userC: z.number(),
  userE: z.number(),
  userA: z.number(),
  userN: z.number(),
  createdAt: z.string(),
  updatedAt: z.string()
})

// Game Schemas
export const GameSessionSchema = z.object({
  sessionId: z.number(),
  userId: z.number(),
  gameType: z.enum(GAME_TYPE_VALUES),
  startedAt: z.string(),
  finishedAt: z.string().optional()
})

export const GameSessionIdSchema = z.object({
  sessionId: z.number()
})

export const CreateGameSessionRequestSchema = z.object({
  gameType: z.enum(GAME_TYPE_VALUES)
})

export const GameResultSchema = z.object({
  sessionId: z.number(),
  userId: z.number(),
  resultO: z.number(),
  resultC: z.number(),
  resultE: z.number(),
  resultA: z.number(),
  resultN: z.number()
})

export const UGResponseRequestSchema = z.object({
  orderId: z.number(),
  money: z.number(),
  isAccepted: z.boolean()
})

export const UGOrderSchema = z.object({
  id: z.string(),
  money: z.enum(MONEY_SIZE_VALUES),
  personaType: z.enum(PERSONA_TYPE_VALUES),
  rate: z.number(),
  roleType: z.number()
})

// Actor Schemas
export const ActorSchema = z.object({
  id: z.number(),
  kind: z.enum(ACTOR_KIND_VALUES),
  personaId: z.number().optional(),
  userId: z.number().optional()
})

// Big Five Codes Schema
export const BigFiveCodeSchema = z.object({
  id: z.number(),
  bigCode: z.enum(PERSONA_CODE_VALUES),
  smallCode: z.string(),
  content: z.string()
})

export const BARTFirstRoundClickRequestSchema = z.object({
  color: z.enum(BART_COLOR_VALUES),
  poppingPoint: z.number(),
  clickIndex: z.number(),
  clickedAt: z.string()
})

export const BARTRoundRequestSchema = z.object({
  clickIndex: z.number(),
  clickedAt: z.string()
})

export const BARTRoundFinishRequestSchema = z.object({
  isPopped: z.boolean()
})

export const GNGClickRequestSchema = z.object({
  stimulusType: z.enum(GNG_STIMULUS_VALUES),
  stimulusStartedAt: z.string(),
  respondedAt: z.string().optional(),
  isSucceeded: z.boolean()
})

// BART Response Schema
export const BARTResponseSchema = z.object({
  id: z.string(),
  sessionId: z.number(),
  roundIndex: z.number(),
  color: z.enum(BART_COLOR_VALUES),
  poppingPoint: z.number(),
  pumpingCnt: z.number().optional(),
  isPopped: z.boolean(),
  playedAt: z.string(),
  finishedAt: z.string().optional()
})

// BART Result Schema
export const BARTResultSchema = z.object({
  sessionId: z.number(),
  totalBalloons: z.number(),
  successBalloons: z.number(),
  failBalloons: z.number(),
  avgPumps: z.number(),
  rewardAmount: z.number(),
  missedReward: z.number(),
  computedAt: z.string()
})

// GNG Response Schema
export const GNGResponseSchema = z.object({
  id: z.string(),
  sessionId: z.number(),
  trialIndex: z.number(),
  stimulusType: z.enum(GNG_STIMULUS_VALUES),
  trialStartedAt: z.string(),
  stimulusAppearedAt: z.string(),
  respondedAt: z.string().optional(),
  isSucceeded: z.boolean().optional()
})

// GNG Result Schema
export const GNGResultSchema = z.object({
  sessionId: z.number(),
  playedAt: z.string(),
  finishedAt: z.string().optional(),
  totalCorrectCnt: z.number(),
  totalIncorrectCnt: z.number(),
  nogoIncorrectCnt: z.number(),
  avgReactionTime: z.number()
})

// UG Response Schema
export const UGResponseSchema = z.object({
  id: z.string(),
  sessionId: z.number(),
  orderId: z.string().optional(),
  money: z.number(),
  isAccepted: z.boolean(),
  finishedAt: z.string().optional()
})

// UG Result Schema
export const UGResultSchema = z.object({
  sessionId: z.number(),
  earnedAmount: z.number(),
  finishedAt: z.string().optional()
})

// UG Round Request Schema
export const UGRoundRequestSchema = z.object({
  orderId: z.number(),
  totalAmount: z.number(),
  isAccepted: z.boolean(),
  proposalRate: z.number()
})

// Friend Chat Messages Schema
export const FriendChatMessageSchema = z.object({
  id: z.number(),
  roomId: z.number(),
  senderActorId: z.number(),
  message: z.string(),
  createdAt: z.string(),
  sender: z.object({
    id: z.number(),
    kind: z.enum(ACTOR_KIND_VALUES),
    userId: z.number().optional(),
    personaId: z.number().optional(),
    persona: z
      .object({
        id: z.number(),
        personaCode: z.string(),
        nickname: z.string()
      })
      .optional()
  })
})

// Diary Chat Request Schema (from OpenAPI)
export const DiaryChatRequestSchema = z.object({
  diaryId: z.number(),
  message: z.string().min(0).max(2000)
})

// Diary Chat Response Schema (from OpenAPI)
export const DiaryChatResponseSchema = z.object({
  id: z.number(),
  diaryId: z.number(),
  message: z.string(),
  senderKind: z.enum(["USER", "PERSONA"]),
  senderActorId: z.number(),
  createdAt: z.string()
})

// Legacy Diary Chat Messages Schema (keep for backward compatibility)
export const DiaryChatMessageSchema = z.object({
  id: z.number(),
  diaryId: z.number(),
  senderActorId: z.number(),
  message: z.string(),
  createdAt: z.string(),
  sender: z.object({
    id: z.number(),
    kind: z.enum(ACTOR_KIND_VALUES),
    userId: z.number().optional(),
    personaId: z.number().optional(),
    persona: z
      .object({
        id: z.number(),
        personaCode: z.string(),
        nickname: z.string()
      })
      .optional()
  })
})

// Survey Response Log Schema
export const SurveyResponseLogSchema = z.object({
  id: z.string(),
  responseId: z.number(),
  value: z.number(),
  occurredAt: z.string(),
  createdAt: z.string()
})

// Friend Invitation Full Schema (with user details)
export const FriendInvitationFullSchema = z.object({
  id: z.number(),
  inviterUserId: z.number(),
  inviteeUserId: z.number(),
  status: z.enum(INVITATION_STATUS_VALUES),
  token: z.string(),
  createdAt: z.string(),
  respondedAt: z.string().optional(),
  deletedAt: z.string().optional(),
  inviter: z.object({
    id: z.number(),
    email: z.string().regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, "유효한 이메일을 입력해주세요"),
    nickname: z.string(),
    profileImageUrl: z.string().optional()
  }),
  invitee: z.object({
    id: z.number(),
    email: z.string().regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, "유효한 이메일을 입력해주세요"),
    nickname: z.string(),
    profileImageUrl: z.string().optional()
  })
})

// Diary Schemas (matching OpenAPI DiaryResponse)
export const DiarySchema = z.object({
  id: z.number(),
  userId: z.number(),
  title: z.string(),
  content: z.string(),
  diaryDate: z.string(), // format: date
  createdAt: z.string(), // format: date-time
  updatedAt: z.string() // format: date-time
})

export const CreateDiaryRequestSchema = z.object({
  title: z.string().min(0).max(150, "제목은 150자 이하로 입력해주세요"),
  content: z.string().min(1, "내용을 입력해주세요"),
  diaryDate: z.string().min(1, "날짜를 선택해주세요") // format: date
})

export const UpdateDiaryRequestSchema = z.object({
  title: z.string().optional(),
  content: z.string().optional(),
  diaryDate: z.string().optional()
})

// DiaryAnalysis Schema (updated to match backend DiaryAnalysisSummary entity)
export const DiaryAnalysisSchema = z.object({
  // id: z.number(),
  diaryId: z.number(),
  oceanMessages: z.array(
    z.object({
      id: z.number(),
      createdAt: z.string(),
      message: z.string(),
      messageOrder: z.number(),
      personality: z.enum(BIG5_INDEX_VALUES),
      personalityName: z.enum(BIG5_INDEX_VALUES_KO)
    })
  ),
  summary: z.object({
    big5Scores: z.object({
      agreeableness: z.float32(),
      conscientiousness: z.float32(),
      extroversion: z.float32(),
      neuroticism: z.float32(),
      openness: z.float32()
    }),
    domainClassification: z.enum(BIG5_INDEX_VALUES).optional(),
    finalConclusion: z.string().optional(),
    keywords: z.array(z.string()).optional() // List<String>
  })
})

export const DiaryCalendarResponseSchema = z.object({
  yearMonth: z.string(),
  diaryDates: z.array(z.string()) // format: date
})

// Friend Schemas
export const FriendSchema = z.object({
  id: z.number(),
  userId: z.number(),
  friendId: z.number(),
  createdAt: z.string(),
  deletedAt: z.string().optional(),
  friend: z.object({
    id: z.number(),
    email: z.string().regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, "유효한 이메일을 입력해주세요"),
    nickname: z.string(),
    profileImageUrl: z.string().optional()
  })
})

export const FriendResponseSchema = z.object({
  id: z.number(),
  userId: z.number(),
  friendId: z.number(),
  friendNickname: z.string(),
  friendProfileImageUrl: z.string(),
  hasPersona: z.boolean(),
  createdAt: z.string()
})

export const FriendsResponseSchema = z.array(FriendResponseSchema)

export const FriendInvitationSchema = z.object({
  id: z.number(),
  inviterUserId: z.number(),
  inviteeUserId: z.number(),
  status: z.enum(INVITATION_STATUS_VALUES),
  token: z.string(),
  createdAt: z.string(),
  respondedAt: z.string().optional()
})

export const CreateInviteResponseSchema = z.object({
  token: z.string(),
  inviteUrl: z.string()
})

// Chat Schemas
export const ChatMessageSchema = z.object({
  id: z.number(),
  roomId: z.number().optional(),
  userId: z.number().optional(),
  senderActorId: z.number(),
  message: z.string(),
  createdAt: z.string()
})

export const SendMessageRequestSchema = z.object({
  message: z.string().min(1, "메시지를 입력해주세요")
})

export const MyChatMessageSchema = z.object({
  id: z.number(),
  userId: z.number(),
  senderActorId: z.number(),
  message: z.string(),
  createdAt: z.string(),
  sender: z.object({
    id: z.number(),
    kind: z.enum(ACTOR_KIND_VALUES),
    userId: z.number().optional(),
    personaId: z.number().optional(),
    persona: z
      .object({
        id: z.number(),
        personaCode: z.string(),
        nickname: z.string()
      })
      .optional()
  })
})

// Survey Schemas
export const SurveySchema = z.object({
  id: z.number(),
  bigFiveId: z.number(),
  questionText: z.string(),
  isReverseScored: z.boolean(),
  bigFiveCode: z.object({
    id: z.number(),
    bigCode: z.enum(PERSONA_CODE_VALUES),
    smallCode: z.string(),
    content: z.string()
  })
})

export const SurveyRequestSchema = z.object({
  page: z.number()
})

export const SurveyQuestionSchema = z.object({
  id: z.number(),
  questionText: z.string()
})

export const SurveyResponseSchema = z.object({
  id: z.number(),
  userId: z.number(),
  surveyId: z.number(),
  value: z.number(),
  startedAt: z.string()
})

export const SurveyResponseLogRequestSchema = z.object({
  surveyId: z.number(),
  value: z.number()
})

export const CompleteSurveyRequestSchema = z.object({
  responses: z.array(
    z.object({
      surveyId: z.number(),
      value: z.number()
    })
  )
})

// Report Schemas
export const ReportSchema = z.object({
  id: z.number(),
  userId: z.number(),
  reportType: z.enum(REPORT_TYPE_VALUES),
  content: z.record(z.string(), z.any()),
  createdAt: z.string()
})

export const ReportResponseSchema = z.object({
  isSuccess: z.boolean(),
  code: z.string(),
  message: z.string(),
  result: z.object({
    id: z.number(),
    userId: z.number(),
    reportType: z.enum(REPORT_TYPE_VALUES),
    content: z.record(z.string(), z.any()),
    createdAt: z.string()
  })
})

// Notification Schemas
export const NotificationSchema = z.object({
  id: z.number(),
  userId: z.number(),
  type: z.string(),
  title: z.string(),
  content: z.string(),
  isRead: z.boolean(),
  createdAt: z.string(),
  readAt: z.string().optional()
})

export const MarkAsReadRequestSchema = z.object({
  notificationIds: z.array(z.number())
})

export const NotificationCountResponseSchema = z.object({
  count: z.number()
})

// Common Schemas
export const PaginationParamsSchema = z.object({
  page: z.number().optional(),
  limit: z.number().optional()
})

export const PaginatedResponseSchema = <T extends z.ZodTypeAny>(dataSchema: T) =>
  z.object({
    data: z.array(dataSchema),
    total: z.number(),
    page: z.number(),
    limit: z.number(),
    totalPages: z.number()
  })

export const ApiResponseSchema = <T extends z.ZodTypeAny>(dataSchema: T) =>
  z.object({
    isSuccess: z.boolean(),
    data: dataSchema.optional(),
    error: z.string().optional(),
    message: z.string().optional()
  })

// Query Options Schemas
export const DiaryQueryOptionsSchema = PaginationParamsSchema.extend({
  sortBy: z.enum(["createdAt", "diaryDate"]).optional(),
  order: z.enum(["asc", "desc"]).optional()
})

export const NotificationQueryOptionsSchema = PaginationParamsSchema.extend({
  isRead: z.boolean().optional()
})

export const ChatQueryOptionsSchema = PaginationParamsSchema.extend({
  // 추가 필터 옵션들
})
