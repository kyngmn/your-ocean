import { BIG5_INDEX_VALUES, getBig5IndexName } from "@/types/enums"

import A from "@/public/characters/A.png"
import C from "@/public/characters/C.png"
import E from "@/public/characters/E.png"
import Image from "next/image"
import N from "@/public/characters/N.png"
import O from "@/public/characters/O.png"
import { cn } from "@/lib/utils"

interface PersonaAvatarProps {
  personality: (typeof BIG5_INDEX_VALUES)[number]
  size?: "sm" | "md" | "lg"
  className?: string
  showBorder?: boolean
}

const AVATAR_IMAGES = {
  OPENNESS: O,
  CONSCIENTIOUSNESS: C,
  EXTROVERSION: E,
  AGREEABLENESS: A,
  NEUROTICISM: N
}

const SIZE_CONFIG = {
  sm: { width: 40, height: 40 },
  md: { width: 80, height: 80 },
  lg: { width: 240, height: 240 }
}

const BORDER_STYLES = {
  OPENNESS: "bg-big-five-O/20 border-big-five-O-dark/30",
  CONSCIENTIOUSNESS: "bg-big-five-C/20 border-big-five-C-dark/30",
  EXTROVERSION: "bg-big-five-E/20 border-big-five-E-dark/30",
  AGREEABLENESS: "bg-big-five-A/20 border-big-five-A-dark/30",
  NEUROTICISM: "bg-big-five-N/20 border-big-five-N-dark/30"
}

export default function PersonaAvatar({ personality, size = "md", className, showBorder = false }: PersonaAvatarProps) {
  const { width, height } = SIZE_CONFIG[size]
  const avatarImage = AVATAR_IMAGES[personality]

  return (
    <Image
      alt={getBig5IndexName(personality)}
      src={avatarImage}
      width={width}
      height={height}
      className={cn(
        showBorder && ["p-1 bg-cover object-center rounded-full border", BORDER_STYLES[personality]],
        className
      )}
    />
  )
}

// 페르소나 코드로부터 PersonalityType 매핑하는 유틸리티 함수
export function getPersonalityFromCode(personaCode?: string): (typeof BIG5_INDEX_VALUES)[number] | null {
  if (!personaCode) return null

  // 페르소나 코드에서 첫 글자로 성격 타입 결정
  const firstChar = personaCode.charAt(0).toUpperCase()

  switch (firstChar) {
    case "O":
      return "OPENNESS"
    case "C":
      return "CONSCIENTIOUSNESS"
    case "E":
      return "EXTROVERSION"
    case "A":
      return "AGREEABLENESS"
    case "N":
      return "NEUROTICISM"
    default:
      return null
  }
}
