"use client"

import CardSection from "@/components/common/CardSection"
import Header from "@/components/layout/Header"
import Navbar from "@/components/layout/Navbar"
import { useRouter } from "next/navigation"

export default function GamePage() {
  const router = useRouter()

  const goBubble = () => {
    router.push("/games/bubble")
  }
  const goShark = () => {
    router.push("/games/shark")
  }
  const goTreasure = () => {
    router.push("/games/treasure")
  }

  return (
    <>
      <Header title="게임" />
      <main className="page has-header has-bottom-nav">
        <div className="section flex flex-col gap-8">
          {/* 버블 게임 */}
          <div>
            <CardSection
              title="버블 게임"
              isLottie={true}
              src="/animations/bubble1.lottie"
              description={
                <>
                  버블에 바람을 넣을 수록 점수가 올라가요! 하지만 너무 욕심내면 버블이 펑- 하고 터져서 점수가 0점이
                  돼버려요. <br />
                  <b>언제 멈출지는 당신의 선택이에요! </b>
                </>
              }
              buttonText="하러가기"
              buttonVariant="default"
              buttonOnClick={goBubble}
            ></CardSection>
          </div>
          {/* 상어를 피하자 */}
          <div>
            <CardSection
              title="상어를 피하자"
              isLottie={true}
              src="/animations/seaplant.lottie"
              description={
                <>
                  깊은 바다속, 무시무시한 상어가 나타났어요! <br />
                  상어가 안 보일 때는 빨리 해초속으로 숨어야 해요. 하지만 상어가 있을 떈 절대 움직이면 안 돼요! <br />
                  <b>빠르고 정확하게 반응</b>할 수 있을까요?
                </>
              }
              buttonText="하러가기"
              buttonVariant="default"
              buttonOnClick={goShark}
            ></CardSection>
          </div>
          {/* 보물 나누기*/}
          <div>
            <CardSection
              title="보물 나누기"
              isLottie={true}
              src="/animations/treasure.lottie"
              description={
                <>
                  깊은 바닷속에서 반짝이는 보물을 발견했어요! <br />
                  보물을 나누어 가지기 위해 서로 제안하고 받아들이며 협의해야 해요 <br />
                  만약 제안이 불공정하다고 느껴져 거절한다면, <b>둘 다 보물을 잃어버려요.</b> <br />
                  <b>얼마나 공정하게</b> 나누어 가질 수 있을까요?
                </>
              }
              buttonText="하러가기"
              buttonVariant="default"
              buttonOnClick={goTreasure}
            ></CardSection>
          </div>
        </div>
      </main>
      <Navbar />
    </>
  )
}
