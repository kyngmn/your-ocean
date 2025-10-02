// Enum 상수 배열들과 한글 변환 함수들

// Big 5
export const BIG5_INDEX_VALUES = [
  "OPENNESS",
  "CONSCIENTIOUSNESS",
  "EXTROVERSION",
  "AGREEABLENESS",
  "NEUROTICISM"
] as const
export type Big5Index = (typeof BIG5_INDEX_VALUES)[number]
export const BIG5_INDEX_LABELS: Record<Big5Index, string> = {
  OPENNESS: "개방밍",
  NEUROTICISM: "신경밍",
  EXTROVERSION: "외향밍",
  AGREEABLENESS: "우호밍",
  CONSCIENTIOUSNESS: "성실밍"
}
export const BIG5_INDEX_VALUES_KO = ["개방성", "신경성", "외향성", "우호성", "성실성"] as const
export type Big5IndexName = (typeof BIG5_INDEX_VALUES_KO)[number]
export const BIG5_INDEX_LABELS_KO: Record<Big5Index, string> = {
  OPENNESS: "개방성",
  NEUROTICISM: "신경성",
  EXTROVERSION: "외향성",
  AGREEABLENESS: "우호성",
  CONSCIENTIOUSNESS: "성실성"
}
export function getBig5IndexLabel(index: Big5Index): string {
  return BIG5_INDEX_LABELS[index]
}
export function getBig5IndexName(index: Big5Index): string {
  return BIG5_INDEX_LABELS_KO[index]
}

// AI Status
export const AI_STATUS_VALUES = ["UNSET", "GENERATING", "GENERATED"] as const
export type AIStatus = (typeof AI_STATUS_VALUES)[number]

export const AI_STATUS_LABELS: Record<AIStatus, string> = {
  UNSET: "미설정",
  GENERATING: "생성중",
  GENERATED: "생성완료"
}

export function getAIStatusLabel(status: AIStatus): string {
  return AI_STATUS_LABELS[status]
}

// Persona Code
export const PERSONA_CODE_VALUES = ["O", "C", "E", "A", "N"] as const
export const PERSONA_CODE_O_SMALL_CODES = ["O1", "O2", "O3", "O4", "O5", "O6"] as const
export const PERSONA_CODE_C_SMALL_CODES = ["C1", "C2", "C3", "C4", "C5", "C6"] as const
export const PERSONA_CODE_E_SMALL_CODES = ["E1", "E2", "E3", "E4", "E5", "E6"] as const
export const PERSONA_CODE_A_SMALL_CODES = ["A1", "A2", "A3", "A4", "A5", "A6"] as const
export const PERSONA_CODE_N_SMALL_CODES = ["N1", "N2", "N3", "N4", "N5", "N6"] as const
export type PersonaCode = (typeof PERSONA_CODE_VALUES)[number]
export type PersonaOSmallCode = (typeof PERSONA_CODE_O_SMALL_CODES)[number]
export type PersonaCSmallCode = (typeof PERSONA_CODE_C_SMALL_CODES)[number]
export type PersonaESmallCode = (typeof PERSONA_CODE_E_SMALL_CODES)[number]
export type PersonaASmallCode = (typeof PERSONA_CODE_A_SMALL_CODES)[number]
export type PersonaNSmallCode = (typeof PERSONA_CODE_N_SMALL_CODES)[number]

export const PERSONA_CODE_LABELS: Record<PersonaCode, string> = {
  O: "개방성",
  C: "성실성",
  E: "외향성",
  A: "친화성",
  N: "신경성"
}

export const PERSONA_CODE_O_SMALL_CODES_LABELS: Record<PersonaOSmallCode, string> = {
  O1: "상상력",
  O2: "예술적 관심",
  O3: "감정성",
  O4: "모험심",
  O5: "지적 호기심",
  O6: "자유주의"
}

export const PERSONA_CODE_C_SMALL_CODES_LABELS: Record<PersonaCSmallCode, string> = {
  C1: "자신감",
  C2: "계획성",
  C3: "책임감",
  C4: "성취 추구",
  C5: "자제력",
  C6: "신중함"
}
export const PERSONA_CODE_E_SMALL_CODES_LABELS: Record<PersonaESmallCode, string> = {
  E1: "친밀감",
  E2: "사교성",
  E3: "자기주장",
  E4: "활동성",
  E5: "흥미 추구",
  E6: "쾌활함"
}
export const PERSONA_CODE_A_SMALL_CODES_LABELS: Record<PersonaASmallCode, string> = {
  A1: "신뢰",
  A2: "도덕성",
  A3: "이타심",
  A4: "협력",
  A5: "겸손",
  A6: "공감"
}
export const PERSONA_CODE_N_SMALL_CODES_LABELS: Record<PersonaNSmallCode, string> = {
  N1: "불안",
  N2: "분노",
  N3: "우울",
  N4: "자의식",
  N5: "충동성",
  N6: "심약함"
}

export function getPersonaCodeLabel(code: PersonaCode): string {
  return PERSONA_CODE_LABELS[code]
}

// Game Type
export const GAME_TYPE_VALUES = ["BART", "GNG", "UG"] as const
export type GameType = (typeof GAME_TYPE_VALUES)[number]

export const GAME_TYPE_LABELS: Record<GameType, string> = {
  BART: "풍선 과제",
  GNG: "고/노고 과제",
  UG: "최후통첩 게임"
}

export function getGameTypeLabel(type: GameType): string {
  return GAME_TYPE_LABELS[type]
}

// BART Colors
export const BART_COLOR_VALUES = ["RED", "BLUE", "GREEN"] as const
export type BARTColor = (typeof BART_COLOR_VALUES)[number]

export const BART_COLOR_LABELS: Record<BARTColor, string> = {
  RED: "빨간색",
  BLUE: "파란색",
  GREEN: "초록색"
}

export function getBARTColorLabel(color: BARTColor): string {
  return BART_COLOR_LABELS[color]
}

// GNG Stimulus Type
export const GNG_STIMULUS_VALUES = ["GO", "NOGO"] as const
export type GNGStimulusType = (typeof GNG_STIMULUS_VALUES)[number]

export const GNG_STIMULUS_LABELS: Record<GNGStimulusType, string> = {
  GO: "반응",
  NOGO: "무반응"
}

export function getGNGStimulusLabel(type: GNGStimulusType): string {
  return GNG_STIMULUS_LABELS[type]
}

// Friend Invitation Status
export const INVITATION_STATUS_VALUES = ["PENDING", "ACCEPTED", "DECLINED"] as const
export type InvitationStatus = (typeof INVITATION_STATUS_VALUES)[number]

export const INVITATION_STATUS_LABELS: Record<InvitationStatus, string> = {
  PENDING: "대기중",
  ACCEPTED: "수락됨",
  DECLINED: "거절됨"
}

export function getInvitationStatusLabel(status: InvitationStatus): string {
  return INVITATION_STATUS_LABELS[status]
}

// Chat Actor Kind
export const ACTOR_KIND_VALUES = ["USER", "PERSONA"] as const
export type ActorKind = (typeof ACTOR_KIND_VALUES)[number]

export const ACTOR_KIND_LABELS: Record<ActorKind, string> = {
  USER: "사용자",
  PERSONA: "페르소나"
}

export function getActorKindLabel(kind: ActorKind): string {
  return ACTOR_KIND_LABELS[kind]
}

// Report Type
export const REPORT_TYPE_VALUES = ["SELF", "FINAL"] as const
export type ReportType = (typeof REPORT_TYPE_VALUES)[number]

export const REPORT_TYPE_LABELS: Record<ReportType, string> = {
  SELF: "자가 리포트",
  FINAL: "최종 리포트"
}

export function getReportTypeLabel(type: ReportType): string {
  return REPORT_TYPE_LABELS[type]
}

// UG Money Size
export const MONEY_SIZE_VALUES = ["고액", "소액"] as const
export type MoneySize = (typeof MONEY_SIZE_VALUES)[number]

export const MONEY_SIZE_LABELS: Record<MoneySize, string> = {
  고액: "고액",
  소액: "소액"
}

export function getMoneySizeLabel(size: MoneySize): string {
  return MONEY_SIZE_LABELS[size]
}

// UG Persona Type
export const PERSONA_TYPE_VALUES = ["가족", "낯선사람", "친구"] as const
export type PersonaType = (typeof PERSONA_TYPE_VALUES)[number]

export const PERSONA_TYPE_LABELS: Record<PersonaType, string> = {
  가족: "가족",
  낯선사람: "낯선사람",
  친구: "친구"
}

export function getPersonaTypeLabel(type: PersonaType): string {
  return PERSONA_TYPE_LABELS[type]
}

// Sort Order
export const SORT_ORDER_VALUES = ["asc", "desc"] as const
export type SortOrder = (typeof SORT_ORDER_VALUES)[number]

export const SORT_ORDER_LABELS: Record<SortOrder, string> = {
  asc: "오름차순",
  desc: "내림차순"
}

export function getSortOrderLabel(order: SortOrder): string {
  return SORT_ORDER_LABELS[order]
}

// Diary Sort Fields
export const DIARY_SORT_VALUES = ["createdAt", "diaryDate"] as const
export type DiarySortField = (typeof DIARY_SORT_VALUES)[number]

export const DIARY_SORT_LABELS: Record<DiarySortField, string> = {
  createdAt: "작성일",
  diaryDate: "일기 날짜"
}

export function getDiarySortLabel(field: DiarySortField): string {
  return DIARY_SORT_LABELS[field]
}

// 모든 enum 값들을 검증하는 유틸리티 함수들
export function isValidAIStatus(value: string): value is AIStatus {
  return AI_STATUS_VALUES.includes(value as AIStatus)
}

export function isValidPersonaCode(value: string): value is PersonaCode {
  return PERSONA_CODE_VALUES.includes(value as PersonaCode)
}

export function isValidPersonaOSmallCode(value: string): value is PersonaOSmallCode {
  return PERSONA_CODE_O_SMALL_CODES.includes(value as PersonaOSmallCode)
}

export function isValidPersonaCSmallCode(value: string): value is PersonaCSmallCode {
  return PERSONA_CODE_C_SMALL_CODES.includes(value as PersonaCSmallCode)
}

export function isValidPersonaESmallCode(value: string): value is PersonaESmallCode {
  return PERSONA_CODE_E_SMALL_CODES.includes(value as PersonaESmallCode)
}

export function isValidPersonaASmallCode(value: string): value is PersonaASmallCode {
  return PERSONA_CODE_A_SMALL_CODES.includes(value as PersonaASmallCode)
}

export function isValidPersonaNSmallCode(value: string): value is PersonaNSmallCode {
  return PERSONA_CODE_N_SMALL_CODES.includes(value as PersonaNSmallCode)
}

export function isValidGameType(value: string): value is GameType {
  return GAME_TYPE_VALUES.includes(value as GameType)
}

export function isValidBARTColor(value: string): value is BARTColor {
  return BART_COLOR_VALUES.includes(value as BARTColor)
}

export function isValidGNGStimulusType(value: string): value is GNGStimulusType {
  return GNG_STIMULUS_VALUES.includes(value as GNGStimulusType)
}

export function isValidInvitationStatus(value: string): value is InvitationStatus {
  return INVITATION_STATUS_VALUES.includes(value as InvitationStatus)
}

export function isValidActorKind(value: string): value is ActorKind {
  return ACTOR_KIND_VALUES.includes(value as ActorKind)
}

export function isValidReportType(value: string): value is ReportType {
  return REPORT_TYPE_VALUES.includes(value as ReportType)
}

export function isValidSortOrder(value: string): value is SortOrder {
  return SORT_ORDER_VALUES.includes(value as SortOrder)
}

export function isValidDiarySortField(value: string): value is DiarySortField {
  return DIARY_SORT_VALUES.includes(value as DiarySortField)
}

export function isValidMoneySize(value: string): value is MoneySize {
  return MONEY_SIZE_VALUES.includes(value as MoneySize)
}

export function isValidPersonaType(value: string): value is PersonaType {
  return PERSONA_TYPE_VALUES.includes(value as PersonaType)
}
