"use client"

import { useState } from "react"
import CardSection from "@/components/common/CardSection"
import { Button } from "@/components/ui/button"

// 게임 가이드 
const gameGuideSteps = [
  {
    title: "게임 방법 (1/3)",
    src: "/animations/bubble1.lottie",
    description: (
      <>
        <b> 바람 넣기 </b> 를 누르면 버블이 점점 불어나면서 점수가 올라가요! <br />
        언제 멈출지를 결정해서 점수를 얻어갈 수 있어요
      </>
    )
  },
  {
    title: "게임 방법 (2/3)",
    src: "/animations/bubble_burst2.lottie",
    description: (
      <>
        하지만 너무 욕심내면 <b>버블이 터져서</b> 해당 라운드 점수는 <b>0점</b>이 돼요
      </>
    )
  },
  {
    title: "게임 방법 (3/3)",
    src: "/animations/bubble1.lottie",
    description: (
      <>
        버블은 <b>총 30개</b>로 각 버블은 언제 터질지 몰라요. <b>언제 멈출지는 당신의 선택!</b>
      </>
    )
  }
]

interface BubbleGameGuideProps {
  onStartGame: () => void
}

export default function BubbleGameGuide({ onStartGame }: BubbleGameGuideProps) {
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
          isLottie={true}
          src={currentGuide.src}
          description={currentGuide.description}
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
