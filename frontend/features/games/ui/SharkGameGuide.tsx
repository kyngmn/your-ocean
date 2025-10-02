"use client"

import { useState } from "react"
import CardSection from "@/components/common/CardSection"
import { Button } from "@/components/ui/button"

// 게임 가이드
const gameGuideSteps = [
  {
    title: "게임 방법 (1/3)",
    isLottie: true,
    src: "/animations/seaplant.lottie",
    description: (
      <>
        화면에 <b>해초가</b> 나타나면 <b>스페이스바</b>를 눌러 해초 속에 숨어야해요! <br />
      </>
    )
  },
  {
    title: "게임 방법 (2/3)",
    isLottie: false,
    src: "/characters/shark.png",
    isAnimate: "animate-fade-in",
    description: (
      <>
        하지만 <b>상어</b>가 나타날 땐 절대 누르면 안돼요!
      </>
    )
  },
  {
    title: "게임 방법 (3/3)",
    isLottie: false,
    src: "/characters/shark.png",
    isAnimate: "animate-bounce",
    description: (
      <>
        빠르게 숨어야 하지만, 잘못 누르면 상어에게 들켜버릴 수도 있어요 <br />
        <b>과연 얼마나 오래 살아남을 수 있을까요?</b>
      </>
    )
  }
]

interface SharkGameGuideProps {
  onStartGame: () => void
}

export default function SharkGameGuide({ onStartGame }: SharkGameGuideProps) {
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
