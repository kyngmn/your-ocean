import PersonaAvatar, { getPersonalityFromCode } from "./PersonaAvatar"

import { PERSONA_CODE_VALUES } from "@/types/enums"
import Typography from "../ui/Typography"
import { cn } from "@/lib/utils"
import { getFromNow } from "@/lib/date"

interface ChatMessageProps {
  message: string
  senderKind: "USER" | "OPPONENT" | "PERSONA"
  senderActorId: number
  createdAt: Date | string
}

export default function ChatMessage({ message, senderKind, senderActorId, createdAt }: ChatMessageProps) {
  // Big Five 지표에 따른 아바타 이름 매핑 (persona 타입일 때만 사용)
  const getAvatarNameByBigFive = (bigFive: string) => {
    const avatarNameMap: { [key: string]: string } = {
      O: "개방밍",
      C: "성실밍",
      E: "외향밍",
      A: "우호밍",
      N: "신경밍"
    }
    return avatarNameMap[bigFive] || "random"
  }

  const personaCode = PERSONA_CODE_VALUES?.[senderActorId - 1]
  if (senderKind === "PERSONA" && senderActorId) {
    const personality = getPersonalityFromCode(personaCode)
    const avatarName = getAvatarNameByBigFive(personaCode?.[0])

    return (
      <div className="flex items-start gap-3 rounded-lg">
        {/* 아바타 */}
        {personality && <PersonaAvatar personality={personality} size="sm" />}

        {/* 메시지와 Big Five 정보 */}
        <div className="flex-1 min-w-0 space-y-1 max-w-11/12">
          <div className="flex items-center gap-2">
            <Typography type="small" className="font-medium">
              {avatarName}
            </Typography>
          </div>
          <div
            className={cn(
              personaCode === "O"
                ? "bg-big-five-O/15"
                : personaCode === "C"
                  ? "bg-big-five-C/15"
                  : personaCode === "E"
                    ? "bg-big-five-E/15"
                    : personaCode === "A"
                      ? "bg-big-five-A/15"
                      : personaCode === "N"
                        ? "bg-big-five-N/15"
                        : "bg-gray-50",
              "rounded-lg p-3"
            )}
          >
            <Typography type="p">{message}</Typography>
          </div>
          {createdAt && (
            <p className="ml-auto mr-1 text-xs font-semibold text-muted-foreground">{getFromNow(createdAt)}</p>
          )}
        </div>
      </div>
    )
  }

  return (
    <div className="flex flex-col space-y-1 max-w-5/6 ml-auto mr-0">
      {senderKind === "USER" ? (
        <div className="rounded-lg p-3 bg-gray-50">
          <Typography type="p">{message}</Typography>
        </div>
      ) : (
        <div className="bg-gray-50 rounded-lg p-3">
          <Typography type="p">{message}</Typography>
        </div>
      )}
      {createdAt && <p className="ml-auto mr-1 text-xs font-semibold text-muted-foreground">{getFromNow(createdAt)}</p>}
    </div>
  )
}
