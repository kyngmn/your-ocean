// Mock 데이터 - schema.ts와 seed.sql을 기반으로 생성

import type {
  ActorDTO,
  BARTResponseDTO,
  BARTResultDTO,
  BigFiveCodeDTO,
  CreateInviteResponseDTO,
  DiaryAnalysisDTO,
  DiaryDTO,
  FriendDTO,
  GNGResponseDTO,
  GNGResultDTO,
  MyChatMessageDTO,
  NotificationDTO,
  ReportDTO,
  SurveyDTO,
  UGOrderDTO,
  UGResultDTO,
  UserDTO,
  UserPersonaDTO
} from "@/types/dto"

// =============================================================================
// Users Mock Data
// =============================================================================
export const mockUsers: UserDTO[] = [
  {
    id: 1,
    email: "test1@example.com",
    nickname: "바다사랑",
    profileImageUrl: "https://example.com/avatar1.jpg",
    provider: "google",
    socialId: "google_123456",
    aiStatus: "UNSET",
    createdAt: "2024-01-15T09:00:00Z",
    updatedAt: "2024-01-20T14:30:00Z"
  },
  {
    id: 2,
    email: "test2@example.com",
    nickname: "파도타기",
    profileImageUrl: undefined,
    provider: "kakao",
    socialId: "kakao_789012",
    aiStatus: "UNSET",
    createdAt: "2024-01-16T10:15:00Z",
    updatedAt: "2024-01-21T16:45:00Z"
  },
  {
    id: 3,
    email: "test3@example.com",
    nickname: "깊은바다",
    profileImageUrl: "https://example.com/avatar3.jpg",
    provider: "naver",
    socialId: "naver_345678",
    aiStatus: "GENERATING",
    createdAt: "2024-01-17T11:30:00Z",
    updatedAt: "2024-01-22T18:20:00Z"
  },
  {
    id: 4,
    email: "test4@example.com",
    nickname: "산호초",
    profileImageUrl: undefined,
    provider: "google",
    socialId: "google_999999",
    aiStatus: "UNSET",
    createdAt: "2024-01-18T13:45:00Z",
    updatedAt: "2024-01-23T12:10:00Z"
  },
  {
    id: 5,
    email: "test5@example.com",
    nickname: "돌고래",
    profileImageUrl: "https://example.com/avatar5.jpg",
    provider: "kakao",
    socialId: "kakao_888888",
    aiStatus: "UNSET",
    createdAt: "2024-01-19T15:20:00Z"
  }
]

// =============================================================================
// User Personas Mock Data
// =============================================================================
export const mockUserPersonas: UserPersonaDTO[] = [
  {
    id: 1,
    userId: 1,
    personaCode: "O",
    nickname: "호기심이",
    createdAt: "2024-01-20T09:00:00Z",
    updatedAt: "2024-01-20T09:00:00Z"
  },
  {
    id: 2,
    userId: 1,
    personaCode: "C",
    nickname: "성실이",
    createdAt: "2024-01-20T09:01:00Z",
    updatedAt: "2024-01-20T09:01:00Z"
  },
  {
    id: 3,
    userId: 1,
    personaCode: "E",
    nickname: "활발이",
    createdAt: "2024-01-20T09:02:00Z",
    updatedAt: "2024-01-20T09:02:00Z"
  },
  {
    id: 4,
    userId: 2,
    personaCode: "E",
    nickname: "친화력",
    createdAt: "2024-01-21T10:00:00Z",
    updatedAt: "2024-01-21T10:00:00Z"
  },
  {
    id: 5,
    userId: 2,
    personaCode: "A",
    nickname: "배려심",
    createdAt: "2024-01-21T10:01:00Z",
    updatedAt: "2024-01-21T10:01:00Z"
  }
]

// =============================================================================
// Diaries Mock Data
// =============================================================================
export const mockDiaries: DiaryDTO[] = [
  {
    id: 1,
    userId: 1,
    title: "오늘의 감정 일기",
    content:
      "오늘은 정말 기분 좋은 하루였다. 아침부터 날씨가 너무 좋아서 기분이 상쾌했고, 오랜만에 친구들과 함께 바다에 갔다왔다. 파도 소리를 들으며 모래사장을 걸었는데, 발가락 사이로 들어오는 모래의 감촉이 어릴 적 추억을 떠올리게 했다. 친구들과 함께 웃고 떠들며 보낸 시간이 정말 소중하게 느껴졌다. 저녁에는 함께 해산물을 먹으며 오늘 있었던 재미있는 일들을 이야기했다. 이런 순간들이 쌓여서 행복한 삶이 되는 것 같다. 내일도 오늘처럼 감사한 마음으로 하루를 시작해야겠다.",
    diaryDate: "2025-01-20",
    createdAt: "2025-01-20T21:30:00Z",
    updatedAt: "2025-01-20T21:30:00Z"
  },
  {
    id: 2,
    userId: 1,
    title: "새로운 도전",
    content:
      "드디어 새로운 프로젝트를 시작했다. 몇 달 동안 준비하고 고민했던 일인데 막상 시작하니 설레면서도 걱정이 된다. 오늘 첫 회의를 했는데 팀원들이 모두 열정적이어서 든든했다. 각자의 역할을 분담하고 앞으로의 계획을 세웠다. 내가 맡은 부분이 꽤 중요한 역할이라 부담도 되지만, 이것도 성장의 기회라고 생각하니 오히려 동기부여가 된다. 예전의 나였다면 이런 도전을 피했을 텐데, 지금은 불확실함 속에서도 한 발 내딛는 용기를 낼 수 있게 되었다. 앞으로 힘든 순간도 있겠지만 초심을 잃지 않고 끝까지 해내고 싶다. 이 프로젝트를 통해 한 단계 더 성장한 나를 만날 수 있기를 기대한다.",
    diaryDate: "2025-01-21",
    createdAt: "2025-01-21T22:15:00Z",
    updatedAt: "2025-01-21T22:15:00Z"
  },
  {
    id: 3,
    userId: 1,
    title: "휴식의 시간",
    content:
      "오랜만에 아무것도 하지 않고 온전히 쉬는 하루를 보냈다. 평소에는 항상 무언가를 해야 한다는 생각에 쫓기듯 살았는데, 오늘은 그런 모든 것들을 내려놓았다. 아침 늦게 일어나서 좋아하는 커피를 천천히 내려 마시며 창밖을 바라봤다. 하늘에 떠가는 구름을 보면서 멍하니 있었는데, 이런 여유로운 시간이 얼마만인지 모르겠다. 오후에는 소파에 누워 그동안 미뤄뒀던 음악을 들었다. 좋아하는 노래를 들으니 마음이 편안해졌다. 저녁에는 간단한 요리를 해먹고 일찍 잠자리에 들었다. 가끔은 이렇게 아무것도 하지 않는 것이 오히려 많은 것을 하는 것보다 가치있다는 생각이 든다. 내일부터는 다시 바쁜 일상으로 돌아가겠지만, 오늘의 이 여유로움을 기억하며 살아가야겠다.",
    diaryDate: "2025-01-22",
    createdAt: "2025-01-22T20:45:00Z",
    updatedAt: "2025-01-22T20:45:00Z"
  },
  {
    id: 4,
    userId: 2,
    title: "감사한 하루",
    content:
      "오늘 지하철에서 정말 따뜻한 장면을 목격했다. 한 할머니께서 무거운 짐을 들고 계단을 오르시는데, 젊은 청년이 다가가서 짐을 들어드리고 천천히 부축해드렸다. 할머니의 감사하다는 말씀에 청년은 수줍게 웃으며 괜찮다고 했다. 그 모습을 보면서 마음이 따뜻해졌고, 세상은 아직 살만한 곳이라는 생각이 들었다. 회사에서도 동료가 내가 힘들어하는 것을 보고 자기 일이 아닌데도 도와주었다. 평소에는 당연하게 지나쳤을 수도 있는 작은 친절들이 오늘따라 더 크게 다가왔다. 저녁에 집에 오면서 나도 누군가에게 그런 사람이 되어야겠다고 다짐했다. 받은 친절을 다시 베풀고, 따뜻함을 전파하는 사람이 되고 싶다. 오늘 하루 만난 모든 사람들에게 감사한 마음이 든다.",
    diaryDate: "2025-01-20",
    createdAt: "2025-01-20T23:10:00Z",
    updatedAt: "2025-01-20T23:10:00Z"
  },
  {
    id: 5,
    userId: 2,
    title: "운동 시작",
    content:
      "오늘부터 본격적으로 운동을 시작했다. 사실 새해 계획으로 세웠던 것인데 이제야 실행에 옮기게 되었다. 아침 6시에 알람을 맞춰놓고 일어났는데, 평소보다 일찍 일어나니 하루가 길게 느껴졌다. 동네 공원에서 가벼운 조깅으로 시작했다. 처음에는 5분만 뛰어도 숨이 차서 힘들었지만, 천천히 페이스를 조절하니 20분 정도는 뛸 수 있었다. 운동을 마치고 나니 온몸에 땀이 났지만 기분은 상쾌했다. 샤워를 하고 나서 먹은 아침밥이 유독 맛있게 느껴졌다. 오후에는 유튜브를 보면서 홈트레이닝도 따라해봤다. 아직은 동작이 어색하고 힘들지만, 꾸준히 하다보면 익숙해질 것 같다. 운동 일지를 작성하기 시작했는데, 매일 기록하면서 발전하는 모습을 확인하고 싶다. 건강한 몸에 건강한 정신이 깃든다는 말을 믿으며, 이번에는 꼭 꾸준히 해보려고 한다.",
    diaryDate: "2025-01-23",
    createdAt: "2025-01-23T21:00:00Z",
    updatedAt: "2025-01-23T21:00:00Z"
  },
  {
    id: 6,
    userId: 3,
    title: "책 읽기",
    content:
      '오랜만에 서점에 들러 책을 한 권 샀다. 평소 읽고 싶었던 에세이였는데, 제목부터 마음에 들어서 망설임 없이 골랐다. 집에 와서 따뜻한 차를 한 잔 내리고 조용히 책을 펼쳤다. 첫 장부터 작가의 따뜻한 문체에 마음이 편안해졌다. 일상의 소소한 이야기들이 담겨있었는데, 평범한 이야기 속에서 깊은 울림을 느낄 수 있었다. 특히 "행복은 거창한 것이 아니라 매일의 작은 순간들 속에 있다"는 구절이 마음에 남았다. 책을 읽으면서 내 삶도 돌아보게 되었다. 그동안 너무 바쁘게만 살았던 것 같다. 책 속의 주인공처럼 나도 일상 속에서 작은 행복을 찾아보려고 한다. 오늘 저녁 노을이 예뻤던 것, 점심에 먹은 김치찌개가 맛있었던 것, 이런 소소한 것들이 모여 나의 하루를 채워간다는 것을 새삼 느꼈다. 앞으로도 독서하는 시간을 꾸준히 가져야겠다.',
    diaryDate: "2025-01-19",
    createdAt: "2025-01-19T19:30:00Z",
    updatedAt: "2025-01-19T19:30:00Z"
  }
]

// =============================================================================
// Diary Analysis Mock Data
// =============================================================================
export const mockDiaryAnalysis: DiaryAnalysisDTO = {
  diaryId: 1,
  oceanMessages: [
    {
      id: 1,
      createdAt: "2025-01-20T22:00:00Z",
      message: "오늘의 바다 경험은 당신의 개방적인 성격을 잘 보여줍니다. 새로운 환경에 대한 호기심과 자연에 대한 감상이 돋보입니다.",
      messageOrder: 1,
      personality: "OPENNESS",
      personalityName: "개방성"
    },
    {
      id: 2,
      createdAt: "2025-01-20T22:00:00Z",
      message: "친구들과의 약속을 지키고 함께 좋은 시간을 보내는 모습에서 성실함이 드러납니다.",
      messageOrder: 2,
      personality: "CONSCIENTIOUSNESS",
      personalityName: "성실성"
    },
    {
      id: 3,
      createdAt: "2025-01-20T22:00:00Z",
      message: "친구들과 활발하게 소통하고 즐거운 시간을 함께 보내는 당신의 외향적인 면이 잘 나타납니다.",
      messageOrder: 3,
      personality: "EXTROVERSION",
      personalityName: "외향성"
    }
  ],
  summary: {
    big5Scores: {
      openness: 0.75,
      conscientiousness: 0.82,
      extroversion: 0.68,
      agreeableness: 0.79,
      neuroticism: 0.34
    },
    domainClassification: "EXTROVERSION",
    finalConclusion:
      "오늘은 친구들과 함께 바다에서 즐거운 시간을 보낸 특별한 하루였습니다. 파도 소리와 모래사장의 촉감이 어릴 적 추억을 떠올리게 했고, 친구들과 함께 나눈 웃음과 대화가 삶의 소중함을 느끼게 했습니다. 일상의 작은 순간들이 모여 행복을 만든다는 것을 깨달으며, 감사한 마음으로 하루를 마무리했습니다.",
    keywords: ["바다", "친구", "즐거움", "추억", "감사", "행복"]
  }
}

// =============================================================================
// Notifications Mock Data
// =============================================================================
export const mockNotifications: NotificationDTO[] = [
  {
    id: 1,
    userId: 1,
    type: "FRIEND_REQUEST",
    title: "새로운 친구 요청",
    content: "돌고래님이 친구 요청을 보냈습니다.",
    isRead: false,
    createdAt: "2025-01-23T14:30:00Z"
  },
  {
    id: 2,
    userId: 1,
    type: "DIARY_ANALYSIS",
    title: "일기 분석 완료",
    content: '"오늘의 감정 일기" 분석이 완료되었습니다. 페르소나들의 따뜻한 피드백을 확인해보세요!',
    isRead: true,
    createdAt: "2025-01-20T22:00:00Z",
    readAt: "2025-01-21T08:15:00Z"
  },
  {
    id: 3,
    userId: 1,
    type: "GAME_COMPLETE",
    title: "게임 완료",
    content: "BART 게임을 완료했습니다. 결과를 확인해보세요!",
    isRead: true,
    createdAt: "2025-01-19T16:45:00Z",
    readAt: "2025-01-19T17:00:00Z"
  },
  {
    id: 4,
    userId: 2,
    type: "PERSONA_CREATED",
    title: "페르소나 생성 완료",
    content: "당신의 AI 페르소나들이 생성되었습니다! 친화력과 배려심이 당신을 기다리고 있어요.",
    isRead: false,
    createdAt: "2025-01-21T11:00:00Z"
  },
  {
    id: 5,
    userId: 3,
    type: "SURVEY_REMINDER",
    title: "설문 완료 알림",
    content: "성격 분석을 위한 설문이 아직 완료되지 않았습니다. 페르소나 생성을 위해 설문을 완료해주세요.",
    isRead: false,
    createdAt: "2025-01-23T10:00:00Z"
  }
]

// =============================================================================
// Friends Mock Data
// =============================================================================
export const mockFriends: FriendDTO[] = [
  {
    id: 1,
    userId: 1,
    friendId: 2,
    createdAt: "2025-01-18T10:00:00Z",
    friend: {
      id: 2,
      email: "test2@example.com",
      nickname: "파도타기",
      profileImageUrl: undefined
    }
  },
  {
    id: 2,
    userId: 1,
    friendId: 3,
    createdAt: "2025-01-19T15:30:00Z",
    friend: {
      id: 3,
      email: "test3@example.com",
      nickname: "깊은바다",
      profileImageUrl: "https://example.com/avatar3.jpg"
    }
  },
  {
    id: 3,
    userId: 2,
    friendId: 1,
    createdAt: "2025-01-18T10:00:00Z",
    friend: {
      id: 1,
      email: "test1@example.com",
      nickname: "바다사랑",
      profileImageUrl: "https://example.com/avatar1.jpg"
    }
  },
  {
    id: 4,
    userId: 2,
    friendId: 4,
    createdAt: "2025-01-20T12:00:00Z",
    friend: {
      id: 4,
      email: "test4@example.com",
      nickname: "산호초",
      profileImageUrl: undefined
    }
  }
]

// =============================================================================
// My Chat Messages Mock Data
// =============================================================================
export const mockMyChatMessages: MyChatMessageDTO[] = [
  {
    id: 1,
    userId: 1,
    senderActorId: 1,
    message: "안녕하세요! 오늘 기분이 어떠신가요?",
    createdAt: "2025-01-23T09:00:00Z",
    sender: {
      id: 1,
      kind: "USER",
      userId: 1,
      persona: undefined
    }
  },
  {
    id: 2,
    userId: 1,
    senderActorId: 7,
    message: "안녕하세요! 오늘은 특별히 새로운 것을 배우고 싶은 기분이에요. 혹시 관심 있는 주제가 있으신가요?",
    createdAt: "2025-01-23T09:01:00Z",
    sender: {
      id: 7,
      kind: "PERSONA",
      personaId: 1,
      persona: {
        id: 1,
        personaCode: "O",
        nickname: "호기심이"
      }
    }
  },
  {
    id: 3,
    userId: 1,
    senderActorId: 1,
    message: "요즘 독서에 관심이 생겼어요. 좋은 책 추천해주실 수 있나요?",
    createdAt: "2025-01-23T09:02:00Z",
    sender: {
      id: 1,
      kind: "USER",
      userId: 1,
      persona: undefined
    }
  },
  {
    id: 4,
    userId: 1,
    senderActorId: 8,
    message:
      "독서는 정말 좋은 습관이에요! 체계적으로 접근하시면 더 효과적일 거예요. 먼저 관심 분야를 정하고, 입문서부터 시작하는 것을 추천해요.",
    createdAt: "2025-01-23T09:03:00Z",
    sender: {
      id: 8,
      kind: "PERSONA",
      personaId: 2,
      persona: {
        id: 2,
        personaCode: "C",
        nickname: "성실이"
      }
    }
  },
  {
    id: 5,
    userId: 1,
    senderActorId: 9,
    message:
      "저는 사람들과 함께 책을 읽는 독서모임을 추천해요! 혼자 읽는 것도 좋지만, 다른 사람들과 의견을 나누면 더 깊이 있게 이해할 수 있어요.",
    createdAt: "2025-01-23T09:04:00Z",
    sender: {
      id: 9,
      kind: "PERSONA",
      personaId: 3,
      persona: {
        id: 3,
        personaCode: "E",
        nickname: "활발이"
      }
    }
  }
]

// =============================================================================
// Surveys Mock Data
// =============================================================================
export const mockSurveys: SurveyDTO[] = [
  {
    id: 1,
    bigFiveId: 1,
    questionText: "나는 새로운 경험을 추구한다.",
    isReverseScored: false,
    bigFiveCode: {
      id: 1,
      bigCode: "O",
      smallCode: "O1",
      content: "개방성 - 경험에 대한 개방성"
    }
  },
  {
    id: 2,
    bigFiveId: 2,
    questionText: "나는 일을 체계적으로 처리한다.",
    isReverseScored: false,
    bigFiveCode: {
      id: 2,
      bigCode: "C",
      smallCode: "C1",
      content: "성실성 - 체계성"
    }
  },
  {
    id: 3,
    bigFiveId: 3,
    questionText: "나는 사교적이고 외향적이다.",
    isReverseScored: false,
    bigFiveCode: {
      id: 3,
      bigCode: "E",
      smallCode: "E1",
      content: "외향성 - 사교성"
    }
  },
  {
    id: 4,
    bigFiveId: 4,
    questionText: "나는 다른 사람을 믿는 편이다.",
    isReverseScored: false,
    bigFiveCode: {
      id: 4,
      bigCode: "A",
      smallCode: "A1",
      content: "친화성 - 신뢰"
    }
  },
  {
    id: 5,
    bigFiveId: 5,
    questionText: "나는 스트레스를 잘 받는다.",
    isReverseScored: false,
    bigFiveCode: {
      id: 5,
      bigCode: "N",
      smallCode: "N1",
      content: "신경성 - 스트레스 민감성"
    }
  },
  {
    id: 6,
    bigFiveId: 1,
    questionText: "나는 상상력이 풍부하지 않다.",
    isReverseScored: true,
    bigFiveCode: {
      id: 1,
      bigCode: "O",
      smallCode: "O2",
      content: "개방성 - 상상력"
    }
  },
  {
    id: 7,
    bigFiveId: 2,
    questionText: "나는 계획 없이 행동하는 편이다.",
    isReverseScored: true,
    bigFiveCode: {
      id: 2,
      bigCode: "C",
      smallCode: "C2",
      content: "성실성 - 계획성"
    }
  },
  {
    id: 8,
    bigFiveId: 3,
    questionText: "나는 조용하고 내성적이다.",
    isReverseScored: true,
    bigFiveCode: {
      id: 3,
      bigCode: "E",
      smallCode: "E2",
      content: "외향성 - 내향성"
    }
  },
  {
    id: 9,
    bigFiveId: 4,
    questionText: "나는 다른 사람의 단점을 자주 본다.",
    isReverseScored: true,
    bigFiveCode: {
      id: 4,
      bigCode: "A",
      smallCode: "A2",
      content: "친화성 - 관용성"
    }
  },
  {
    id: 10,
    bigFiveId: 5,
    questionText: "나는 감정적으로 안정되어 있다.",
    isReverseScored: true,
    bigFiveCode: {
      id: 5,
      bigCode: "N",
      smallCode: "N2",
      content: "신경성 - 정서적 안정성"
    }
  }
]

// =============================================================================
// Actors Mock Data
// =============================================================================
export const mockActors: ActorDTO[] = [
  {
    id: 1,
    kind: "USER",
    userId: 1,
    personaId: undefined
  },
  {
    id: 2,
    kind: "USER",
    userId: 2,
    personaId: undefined
  },
  {
    id: 3,
    kind: "USER",
    userId: 3,
    personaId: undefined
  },
  {
    id: 7,
    kind: "PERSONA",
    userId: undefined,
    personaId: 1
  },
  {
    id: 8,
    kind: "PERSONA",
    userId: undefined,
    personaId: 2
  },
  {
    id: 9,
    kind: "PERSONA",
    userId: undefined,
    personaId: 3
  }
]

// =============================================================================
// Big Five Codes Mock Data
// =============================================================================
export const mockBigFiveCodes: BigFiveCodeDTO[] = [
  {
    id: 1,
    bigCode: "O",
    smallCode: "O1",
    content: "개방성 - 경험에 대한 개방성"
  },
  {
    id: 2,
    bigCode: "C",
    smallCode: "C1",
    content: "성실성 - 체계성"
  },
  {
    id: 3,
    bigCode: "E",
    smallCode: "E1",
    content: "외향성 - 사교성"
  },
  {
    id: 4,
    bigCode: "A",
    smallCode: "A1",
    content: "친화성 - 신뢰"
  },
  {
    id: 5,
    bigCode: "N",
    smallCode: "N1",
    content: "신경성 - 스트레스 민감성"
  }
]

// =============================================================================
// Game Sessions Mock Data
// =============================================================================
// export const mockGameSessions: GameSessionDTO[] = [
//   {
//     id: 1,
//     userId: 1,
//     gameType: "BART",
//     startedAt: "2025-01-19T14:00:00Z",
//     finishedAt: "2025-01-19T14:30:00Z"
//   },
//   {
//     id: 2,
//     userId: 1,
//     gameType: "GNG",
//     startedAt: "2025-01-19T15:00:00Z",
//     finishedAt: "2025-01-19T15:20:00Z"
//   },
//   {
//     id: 3,
//     userId: 1,
//     gameType: "UG",
//     startedAt: "2025-01-19T16:00:00Z",
//     finishedAt: "2025-01-19T16:45:00Z"
//   },
//   {
//     id: 4,
//     userId: 2,
//     gameType: "BART",
//     startedAt: "2025-01-20T10:00:00Z",
//     finishedAt: "2025-01-20T10:25:00Z"
//   },
//   {
//     id: 5,
//     userId: 3,
//     gameType: "GNG",
//     startedAt: "2025-01-21T13:00:00Z",
//     finishedAt: undefined // 진행 중
//   }
// ]

// =============================================================================
// BART Game Mock Data
// =============================================================================
export const mockBARTResponses: BARTResponseDTO[] = [
  {
    id: "1",
    sessionId: 1,
    roundIndex: 1,
    color: "RED",
    poppingPoint: 25,
    pumpingCnt: 20,
    isPopped: false,
    playedAt: "2025-01-19T14:05:00Z",
    finishedAt: "2025-01-19T14:06:00Z"
  },
  {
    id: "2",
    sessionId: 1,
    roundIndex: 2,
    color: "BLUE",
    poppingPoint: 15,
    pumpingCnt: 18,
    isPopped: true,
    playedAt: "2025-01-19T14:07:00Z",
    finishedAt: "2025-01-19T14:08:00Z"
  },
  {
    id: "3",
    sessionId: 4,
    roundIndex: 1,
    color: "GREEN",
    poppingPoint: 30,
    pumpingCnt: 25,
    isPopped: false,
    playedAt: "2025-01-20T10:05:00Z",
    finishedAt: "2025-01-20T10:06:00Z"
  }
]

export const mockBARTResults: BARTResultDTO[] = [
  {
    sessionId: 1,
    totalBalloons: 30,
    successBalloons: 18,
    failBalloons: 12,
    avgPumps: 22.5,
    rewardAmount: 450,
    missedReward: 300,
    computedAt: "2025-01-19T14:30:00Z"
  },
  {
    sessionId: 4,
    totalBalloons: 30,
    successBalloons: 22,
    failBalloons: 8,
    avgPumps: 26.8,
    rewardAmount: 670,
    missedReward: 160,
    computedAt: "2025-01-20T10:25:00Z"
  }
]

// =============================================================================
// GNG Game Mock Data
// =============================================================================
export const mockGNGResponses: GNGResponseDTO[] = [
  {
    id: "1",
    sessionId: 2,
    trialIndex: 1,
    stimulusType: "GO",
    trialStartedAt: "2025-01-19T15:01:00Z",
    stimulusAppearedAt: "2025-01-19T15:01:02Z",
    respondedAt: "2025-01-19T15:01:02.450Z",
    isSucceeded: true
  },
  {
    id: "2",
    sessionId: 2,
    trialIndex: 2,
    stimulusType: "NOGO",
    trialStartedAt: "2025-01-19T15:01:05Z",
    stimulusAppearedAt: "2025-01-19T15:01:07Z",
    respondedAt: undefined,
    isSucceeded: true
  },
  {
    id: "3",
    sessionId: 5,
    trialIndex: 1,
    stimulusType: "GO",
    trialStartedAt: "2025-01-21T13:01:00Z",
    stimulusAppearedAt: "2025-01-21T13:01:02Z",
    respondedAt: "2025-01-21T13:01:02.380Z",
    isSucceeded: true
  }
]

export const mockGNGResults: GNGResultDTO[] = [
  {
    sessionId: 2,
    playedAt: "2025-01-19T15:00:00Z",
    finishedAt: "2025-01-19T15:20:00Z",
    totalCorrectCnt: 145,
    totalIncorrectCnt: 15,
    nogoIncorrectCnt: 8,
    avgReactionTime: 420.5
  }
]

// =============================================================================
// UG Game Mock Data
// =============================================================================
export const mockUGOrders: UGOrderDTO[] = [
  {
    id: "1",
    money: "고액",
    personaType: "친구",
    rate: 0.3,
    roleType: 1
  },
  {
    id: "2",
    money: "소액",
    personaType: "낯선사람",
    rate: 0.5,
    roleType: 0
  },
  {
    id: "3",
    money: "고액",
    personaType: "가족",
    rate: 0.2,
    roleType: 1
  }
]

// export const mockUGResponses: UGResponseDTO[] = [
//   {
//     id: "1",
//     sessionId: 3,
//     orderId: 1,
//     money: 10000,
//     isAccepted: true,
//     finishedAt: "2025-01-19T16:15:00Z"
//   },
//   {
//     id: "2",
//     sessionId: "3",
//     orderId: "2",
//     money: 2500,
//     isAccepted: false,
//     finishedAt: "2025-01-19T16:25:00Z"
//   }
// ]

export const mockUGResults: UGResultDTO[] = [
  {
    sessionId: 3,
    earnedAmount: 7500,
    finishedAt: "2025-01-19T16:45:00Z"
  }
]
// Treasure Game Mock Data
// =============================================================================
export const mockTreasureGame: Array<{
  id: number
  roleType: number
  personaType: string
  money: string
  rate?: number
}> = [
  { id: 1, roleType: 1, personaType: "FRIEND", money: "LARGE" },
  { id: 2, roleType: 1, personaType: "STRANGER", money: "SMALL" },
  { id: 3, roleType: 1, personaType: "FAMILY", money: "LARGE" },
  { id: 4, roleType: 1, personaType: "FRIEND", money: "SMALL" },
  { id: 5, roleType: 1, personaType: "STRANGER", money: "LARGE" },
  { id: 6, roleType: 1, personaType: "FAMILY", money: "SMALL" },

  { id: 7, roleType: 2, personaType: "STRANGER", money: "LARGE", rate: 1 },
  { id: 8, roleType: 2, personaType: "FAMILY", money: "SMALL", rate: 5 },
  { id: 9, roleType: 2, personaType: "FRIEND", money: "LARGE", rate: 3 },
  { id: 10, roleType: 2, personaType: "STRANGER", money: "SMALL", rate: 7 },
  { id: 11, roleType: 2, personaType: "FAMILY", money: "LARGE", rate: 2 },
  { id: 12, roleType: 2, personaType: "FRIEND", money: "SMALL", rate: 9 },
  { id: 13, roleType: 2, personaType: "STRANGER", money: "LARGE", rate: 3 },
  { id: 14, roleType: 2, personaType: "FAMILY", money: "SMALL", rate: 1 },
  { id: 15, roleType: 2, personaType: "FRIEND", money: "LARGE", rate: 6 },
  { id: 16, roleType: 2, personaType: "FAMILY", money: "LARGE", rate: 9 },
  { id: 17, roleType: 2, personaType: "STRANGER", money: "SMALL", rate: 5 },
  { id: 18, roleType: 2, personaType: "FRIEND", money: "SMALL", rate: 2 },
  { id: 19, roleType: 2, personaType: "FAMILY", money: "SMALL", rate: 8 },
  { id: 20, roleType: 2, personaType: "FRIEND", money: "LARGE", rate: 4 },
  { id: 21, roleType: 2, personaType: "STRANGER", money: "LARGE", rate: 8 },
  { id: 22, roleType: 2, personaType: "STRANGER", money: "SMALL", rate: 4 },
  { id: 23, roleType: 2, personaType: "FAMILY", money: "LARGE", rate: 7 },
  { id: 24, roleType: 2, personaType: "FRIEND", money: "SMALL", rate: 6 },

  { id: 25, roleType: 3, personaType: "STRANGER", money: "LARGE" },
  { id: 26, roleType: 3, personaType: "FAMILY", money: "SMALL" },
  { id: 27, roleType: 3, personaType: "FRIEND", money: "LARGE" },
  { id: 28, roleType: 3, personaType: "STRANGER", money: "SMALL" },
  { id: 29, roleType: 3, personaType: "FAMILY", money: "LARGE" },
  { id: 30, roleType: 3, personaType: "FRIEND", money: "SMALL" }
]

// =============================================================================
// Reports Mock Data
// =============================================================================
export const mockReports: ReportDTO[] = [
  {
    id: 1,
    userId: 1,
    reportType: "SELF",
    content: {
      summary: "당신은 개방적이고 성실하며 외향적인 성격을 가지고 있습니다.",
      personality: {
        openness: 85,
        conscientiousness: 78,
        extraversion: 82,
        agreeableness: 74,
        neuroticism: 32
      },
      smallCodes: {
        O1: { score: 88, description: "상상력이 풍부하고 창의적인 아이디어를 자주 떠올립니다." },
        O2: { score: 82, description: "예술과 미술에 대한 깊은 관심과 감성을 가지고 있습니다." },
        O3: { score: 85, description: "감정을 풍부하게 표현하고 타인의 감정을 잘 이해합니다." },
        O4: { score: 90, description: "새로운 모험과 도전을 적극적으로 추구합니다." },
        O5: { score: 87, description: "지적 호기심이 강하고 새로운 지식을 탐구합니다." },
        O6: { score: 83, description: "자유로운 사고와 독립적인 가치관을 가지고 있습니다." },
        C1: { score: 75, description: "자신의 능력에 대한 확신이 있고 자신감이 높습니다." },
        C2: { score: 80, description: "체계적인 계획을 세우고 실행하는 능력이 뛰어납니다." },
        C3: { score: 78, description: "책임감이 강하고 맡은 일을 끝까지 해냅니다." },
        C4: { score: 82, description: "성취를 추구하고 목표 달성을 위해 노력합니다." },
        C5: { score: 76, description: "자제력이 있어 충동적인 행동을 잘 통제합니다." },
        C6: { score: 79, description: "신중하게 생각하고 결정하는 경향이 있습니다." },
        E1: { score: 85, description: "친밀한 관계를 형성하고 유지하는 능력이 뛰어납니다." },
        E2: { score: 88, description: "사교적이고 사람들과 어울리기를 좋아합니다." },
        E3: { score: 82, description: "자신의 의견을 당당하게 표현합니다." },
        E4: { score: 90, description: "활동적이고 에너지가 넘치는 성격입니다." },
        E5: { score: 87, description: "다양한 흥미와 관심사를 추구합니다." },
        E6: { score: 89, description: "쾌활하고 긍정적인 에너지를 가지고 있습니다." },
        A1: { score: 72, description: "타인을 신뢰하고 긍정적으로 바라봅니다." },
        A2: { score: 75, description: "도덕적 가치관이 확고하고 윤리적입니다." },
        A3: { score: 78, description: "이타심이 강하고 타인을 돕는 것을 좋아합니다." },
        A4: { score: 80, description: "협력적이고 팀워크를 중시합니다." },
        A5: { score: 73, description: "겸손한 자세로 다른 사람의 의견을 존중합니다." },
        A6: { score: 76, description: "타인의 감정과 상황에 공감하는 능력이 뛰어납니다." },
        N1: { score: 25, description: "불안감을 잘 느끼지 않고 안정적입니다." },
        N2: { score: 30, description: "분노를 잘 통제하고 감정 조절이 잘 됩니다." },
        N3: { score: 28, description: "우울감에 잘 빠지지 않고 긍정적입니다." },
        N4: { score: 35, description: "자의식이 적고 자연스러운 모습을 보입니다." },
        N5: { score: 32, description: "충동적이지 않고 신중한 판단을 합니다." },
        N6: { score: 28, description: "심약하지 않고 용감한 면모를 보입니다." }
      },
      strengths: ["새로운 경험에 대한 적극적인 태도", "체계적이고 계획적인 업무 처리", "활발한 사회적 관계 형성"],
      recommendations: [
        "창의적인 활동에 더 많은 시간을 투자해보세요",
        "때로는 완벽주의를 내려놓고 유연성을 가져보세요",
        "혼자만의 시간도 중요하니 균형을 맞춰보세요"
      ]
    },
    createdAt: "2025-01-20T10:00:00Z"
  },
  {
    id: 2,
    userId: 2,
    reportType: "SELF",
    content: {
      summary: "당신은 친화적이고 외향적인 성격으로 사람들과의 관계를 중시합니다.",
      personality: {
        openness: 65,
        conscientiousness: 68,
        extraversion: 88,
        agreeableness: 92,
        neuroticism: 88
      },
      smallCodes: {
        O1: { score: 70, description: "상상력이 있고 창의적인 아이디어를 가끔 떠올립니다." },
        O2: { score: 65, description: "예술과 미술에 대한 관심이 보통 수준입니다." },
        O3: { score: 68, description: "감정을 적절히 표현하고 타인의 감정을 이해합니다." },
        O4: { score: 72, description: "새로운 경험에 대한 관심이 있습니다." },
        O5: { score: 67, description: "지적 호기심이 있고 새로운 지식을 탐구합니다." },
        O6: { score: 63, description: "자유로운 사고를 하되 전통적인 가치도 존중합니다." },
        C1: { score: 65, description: "자신의 능력에 대한 확신이 보통 수준입니다." },
        C2: { score: 70, description: "계획을 세우고 실행하는 능력이 있습니다." },
        C3: { score: 68, description: "책임감이 있고 맡은 일을 해냅니다." },
        C4: { score: 72, description: "성취를 추구하고 목표 달성을 위해 노력합니다." },
        C5: { score: 66, description: "자제력이 있어 충동적인 행동을 어느 정도 통제합니다." },
        C6: { score: 69, description: "신중하게 생각하고 결정하는 경향이 있습니다." },
        E1: { score: 90, description: "친밀한 관계를 형성하고 유지하는 능력이 매우 뛰어납니다." },
        E2: { score: 92, description: "매우 사교적이고 사람들과 어울리기를 좋아합니다." },
        E3: { score: 85, description: "자신의 의견을 당당하게 표현합니다." },
        E4: { score: 88, description: "매우 활동적이고 에너지가 넘치는 성격입니다." },
        E5: { score: 86, description: "다양한 흥미와 관심사를 적극적으로 추구합니다." },
        E6: { score: 91, description: "매우 쾌활하고 긍정적인 에너지를 가지고 있습니다." },
        A1: { score: 95, description: "타인을 매우 신뢰하고 긍정적으로 바라봅니다." },
        A2: { score: 90, description: "도덕적 가치관이 매우 확고하고 윤리적입니다." },
        A3: { score: 88, description: "이타심이 매우 강하고 타인을 돕는 것을 좋아합니다." },
        A4: { score: 92, description: "매우 협력적이고 팀워크를 중시합니다." },
        A5: { score: 85, description: "겸손한 자세로 다른 사람의 의견을 존중합니다." },
        A6: { score: 90, description: "타인의 감정과 상황에 매우 잘 공감합니다." },
        N1: { score: 20, description: "불안감을 거의 느끼지 않고 매우 안정적입니다." },
        N2: { score: 25, description: "분노를 매우 잘 통제하고 감정 조절이 뛰어납니다." },
        N3: { score: 22, description: "우울감에 거의 빠지지 않고 매우 긍정적입니다." },
        N4: { score: 30, description: "자의식이 거의 없고 매우 자연스러운 모습을 보입니다." },
        N5: { score: 28, description: "충동적이지 않고 매우 신중한 판단을 합니다." },
        N6: { score: 25, description: "심약하지 않고 매우 용감한 면모를 보입니다." }
      },
      strengths: ["타인에 대한 깊은 배려와 이해", "뛰어난 사회적 소통 능력", "긍정적인 에너지와 활력"],
      recommendations: [
        "자신의 의견도 당당하게 표현해보세요",
        "때로는 거절하는 것도 필요합니다",
        "개인적인 시간을 통해 에너지를 충전하세요"
      ]
    },
    createdAt: "2025-01-21T15:00:00Z"
  },
  {
    id: 3,
    userId: 3,
    reportType: "FINAL",
    content: {
      summary: "당신은 신중하고 내성적인 성격으로 깊이 있는 사고를 선호합니다.",
      personality: {
        openness: 72,
        conscientiousness: 85,
        extraversion: 35,
        agreeableness: 68,
        neuroticism: 45
      },
      smallCodes: {
        O1: { score: 75, description: "상상력이 있고 창의적인 아이디어를 가끔 떠올립니다." },
        O2: { score: 78, description: "예술과 미술에 대한 관심이 있습니다." },
        O3: { score: 70, description: "감정을 적절히 표현하고 타인의 감정을 이해합니다." },
        O4: { score: 68, description: "새로운 경험에 대한 관심이 보통 수준입니다." },
        O5: { score: 80, description: "지적 호기심이 강하고 새로운 지식을 탐구합니다." },
        O6: { score: 72, description: "자유로운 사고를 하되 전통적인 가치도 존중합니다." },
        C1: { score: 88, description: "자신의 능력에 대한 확신이 매우 높습니다." },
        C2: { score: 90, description: "매우 체계적인 계획을 세우고 실행하는 능력이 뛰어납니다." },
        C3: { score: 92, description: "책임감이 매우 강하고 맡은 일을 끝까지 해냅니다." },
        C4: { score: 85, description: "성취를 추구하고 목표 달성을 위해 노력합니다." },
        C5: { score: 88, description: "자제력이 매우 강해 충동적인 행동을 잘 통제합니다." },
        C6: { score: 90, description: "매우 신중하게 생각하고 결정하는 경향이 있습니다." },
        E1: { score: 40, description: "친밀한 관계를 형성하는 데 시간이 걸립니다." },
        E2: { score: 35, description: "사교적이지 않고 혼자 있는 시간을 선호합니다." },
        E3: { score: 45, description: "자신의 의견을 표현하는 데 신중합니다." },
        E4: { score: 30, description: "활동적이지 않고 조용한 활동을 선호합니다." },
        E5: { score: 38, description: "흥미와 관심사를 제한적으로 추구합니다." },
        E6: { score: 32, description: "쾌활하지 않고 차분한 성격입니다." },
        A1: { score: 65, description: "타인을 신뢰하되 신중한 편입니다." },
        A2: { score: 70, description: "도덕적 가치관이 확고하고 윤리적입니다." },
        A3: { score: 68, description: "이타심이 있고 타인을 돕는 것을 좋아합니다." },
        A4: { score: 72, description: "협력적이고 팀워크를 중시합니다." },
        A5: { score: 75, description: "겸손한 자세로 다른 사람의 의견을 존중합니다." },
        A6: { score: 70, description: "타인의 감정과 상황에 공감하는 능력이 있습니다." },
        N1: { score: 50, description: "불안감을 가끔 느끼고 안정감이 보통 수준입니다." },
        N2: { score: 45, description: "분노를 어느 정도 통제하고 감정 조절이 보통 수준입니다." },
        N3: { score: 48, description: "우울감에 가끔 빠지고 긍정성이 보통 수준입니다." },
        N4: { score: 55, description: "자의식이 있고 자연스러운 모습을 보이려 노력합니다." },
        N5: { score: 42, description: "충동적이지 않고 신중한 판단을 합니다." },
        N6: { score: 48, description: "심약하지 않고 용감한 면모를 보입니다." }
      },
      strengths: ["체계적이고 계획적인 업무 처리", "깊이 있는 사고와 분석력", "신중하고 책임감 있는 태도"],
      recommendations: [
        "때로는 새로운 경험에 도전해보세요",
        "사람들과의 소통을 늘려보세요",
        "완벽주의를 내려놓고 유연성을 가져보세요"
      ]
    },
    createdAt: "2025-01-22T14:30:00Z"
  }
]

// =============================================================================
// Helper Functions
// =============================================================================

// 특정 사용자의 데이터를 가져오는 함수들
export function getUserById(id: number): UserDTO | undefined {
  return mockUsers.find((user) => user.id === id)
}

export function getDiariesByUserId(userId: number): DiaryDTO[] {
  return mockDiaries.filter((diary) => diary.userId === userId)
}

export function getNotificationsByUserId(userId: number): NotificationDTO[] {
  return mockNotifications.filter((notification) => notification.userId === userId)
}

export function getFriendsByUserId(userId: number): FriendDTO[] {
  return mockFriends.filter((friend) => friend.userId === userId)
}

export function getPersonasByUserId(userId: number): UserPersonaDTO[] {
  return mockUserPersonas.filter((persona) => persona.userId === userId)
}

export function getChatMessagesByUserId(userId: number): MyChatMessageDTO[] {
  return mockMyChatMessages.filter((message) => message.userId === userId)
}

// export function getGameSessionsByUserId(userId: number): GameSessionDTO[] {
//   return mockGameSessions.filter((session) => session.userId === userId)
// }

export function getReportsByUserId(userId: number): ReportDTO[] {
  return mockReports.filter((report) => report.userId === userId)
}

// 랜덤 데이터 생성 함수들
export function getRandomUser(): UserDTO {
  return mockUsers[Math.floor(Math.random() * mockUsers.length)]
}

export function getRandomDiary(): DiaryDTO {
  return mockDiaries[Math.floor(Math.random() * mockDiaries.length)]
}

export function generateMockInviteResponse(): CreateInviteResponseDTO {
  return {
    token: `invite_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    inviteUrl: `https://yourocean.app/invite/accept?token=invite_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }
}

// 페이지네이션된 결과를 시뮬레이션하는 함수
export function getPaginatedData<T>(
  data: T[],
  page: number = 1,
  limit: number = 10
): {
  data: T[]
  total: number
  page: number
  limit: number
  totalPages: number
} {
  const startIndex = (page - 1) * limit
  const endIndex = startIndex + limit
  const paginatedData = data.slice(startIndex, endIndex)

  return {
    data: paginatedData,
    total: data.length,
    page,
    limit,
    totalPages: Math.ceil(data.length / limit)
  }
}

// 전체 mock 데이터 export
export const mockData = {
  users: mockUsers,
  userPersonas: mockUserPersonas,
  actors: mockActors,
  bigFiveCodes: mockBigFiveCodes,
  diaries: mockDiaries,
  diaryAnalysis: mockDiaryAnalysis,
  notifications: mockNotifications,
  friends: mockFriends,
  myChatMessages: mockMyChatMessages,
  surveys: mockSurveys,
  // gameSessions: mockGameSessions,
  bartResponses: mockBARTResponses,
  bartResults: mockBARTResults,
  gngResponses: mockGNGResponses,
  gngResults: mockGNGResults,
  ugOrders: mockUGOrders,
  // ugResponses: mockUGResponses,
  ugResults: mockUGResults,
  reports: mockReports
} as const
