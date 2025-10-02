"use client"

import { useState } from "react"
import CardSection from "@/components/common/CardSection"
import { Button } from "@/components/ui/button"

// 게임 가이드
const gameGuideSteps = [
  {
    title: "게임 방법 (1/3)",
    subTitle: "제안자 모드",
    isLottie: true,
    src: "/animations/chat.lottie",
    description: (
      <>
        제안자 모드에서는 보물을 나누는 비율을 제안할 수 있어요. <br />
        단, 이 경우 <span className="text-accent-blue">2가지 경우</span>가 존재해요 <br />
        1. 상대방이 당신이 내민 비율이 <b>불공정</b>하다고 느껴지면 제안을 <b>수락할 수도 안 할 수도 있어요</b> <br />
        2. 상대방은 당신이 내민 비율을 <b>무조건 받아들여요</b>
      </>
    )
  },
  {
    title: "게임 방법 (2/3)",
    subTitle: "응답자 모드",
    isLottie: false,
    src: "/characters/N.png",
    isAnimate: "animate-bounce",
    description: (
      <>
        응답자 모드에서는 상대방이 제안을 해요
        마음에 들면 수락해서 보물을 나누고, 불공평하다고 느껴지면 거절하세요
        단, <b>거절하면 둘 다 한 푼도 가질 수 없어요!</b>
      </>
    )
  },
  {
    title: "게임 방법 (3/3)",
    subTitle: "",
    isLottie: true,
    src: "/animations/money.lottie",
    isAnimate: "animate-bounce",
    description: (
      <>
        공평하게 나눌지 좀 더 욕심을 내볼지는 <b>당신의 선택!</b>
      </>
    )
  }
]

interface TreasureGameGuideProps {
  onStartGame: () => void
}

export default function TreasureGameGuide({ onStartGame }: TreasureGameGuideProps) {
  const [currentStep, setCurrentStep] = useState(0)
  const totalSteps = gameGuideSteps.length

  const handlePrevious = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1)
    }
  }

  const handleNext = () => {
    if (currentStep < totalSteps - 1) {
      setCurrentStep(currentStep + 1)
    }
  }

  const handleStart = () => {
    onStartGame()
  }

  const currentGuide = gameGuideSteps[currentStep]

  return (
    <>
      <div className="flex flex-col gap-8 w-full">
        <div>
          <CardSection
            title={currentGuide.title}
            subtitle={currentGuide.subTitle}
            isLottie={currentGuide.isLottie}
            src={currentGuide.src}
            description={currentGuide.description}
            isAnimate={currentGuide.isAnimate}
            type="gameExplain"
          />
        </div>

        <div className="flex gap-2 w-full">
          {currentStep > 0 && (
            <Button className="flex-1" size="lg" onClick={handlePrevious}>
              이전
            </Button>
          )}

          {currentStep < totalSteps - 1 ? (
            <Button className="flex-1" size="lg" onClick={handleNext}>
              다음
            </Button>
          ) : (
            <Button className="flex-1" size="lg" onClick={handleStart}>
              시작하기
            </Button>
          )}
        </div>
      </div>
    </>
  )
}
