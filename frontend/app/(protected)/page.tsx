"use client"

import { formatDate, getTodayDate } from "@/lib/date"
import { useEffect, useState } from "react"
import { useGameCount, useTodayMessage } from "@/features/home/queries"

import { Badge } from "@/components/ui/badge"
import CardSection from "@/components/common/CardSection"
import DiaryCalendar from "@/features/diaries/ui/DiaryCalendar"
import Header from "@/components/layout/Header"
import Image from "next/image"
import LabelCard from "@/components/common/LabelCard"
import Navbar from "@/components/layout/Navbar"
import Typography from "@/components/ui/Typography"
import { WandSparkles } from "lucide-react"
import { dev } from "@/lib/dev"
import { useAuthStore } from "@/stores/auth-store"
import { useDiaryByDate } from "@/features/diaries/queries"
import { useRouter } from "next/navigation"

export default function Home() {
  const router = useRouter()
  const { user } = useAuthStore()
  const [isPersona, setIsPersona] = useState(false)

  const { data: diary } = useDiaryByDate(getTodayDate())
  const { data: gameCount } = useGameCount()
  const { data: todayMessage } = useTodayMessage()
  dev.log("🔥date", getTodayDate())
  dev.log("🔥diary", diary)
  dev.log("🔥gameCount", gameCount)
  dev.log("🔥user", user)
  dev.log("🔥todayMessage", todayMessage)

  // ======================= 라우팅 =======================
  // 게임 페이지로 이동
  const goGame = () => {
    router.push("/games")
  }

  // ======================= 게임 정보 매핑 =======================
  // 게임 목록
  const Games = [
    {
      name: "버블 게임",
      count: gameCount?.bart
    },
    {
      name: "상어를 피하자",
      count: gameCount?.gng
    },
    {
      name: "보물 나누기",
      count: gameCount?.ug
    }
  ]

  // ======================= 페르소나 정보 매핑 =======================

  // 페르소나 생성 여부 매핑 함수
  const mappingAiStatus = (aiStatus: string) => {
    switch (aiStatus) {
      case "GENERATED":
        return "페르소나 생성 완료"
      case "UNSET":
        return "페르소나 생성 전"
    }
  }

  // 페르소나 생성 여부 매핑 함수
  useEffect(() => {
    if (user?.aiStatus === "GENERATED") {
      setIsPersona(true)
    }
  }, [user])

  // 페르소나 매핑
  const bigfive = [
    {
      key: "O",
      trait: "O",
      traitName: "개방밍",
      image: "/characters/O.png"
    },
    {
      key: "C",
      trait: "C",
      traitName: "성실밍",
      image: "/characters/C.png"
    },
    {
      key: "E",
      trait: "E",
      traitName: "외향밍",
      image: "/characters/E.png"
    },
    {
      key: "A",
      trait: "A",
      traitName: "친화밍",
      image: "/characters/A.png"
    },
    {
      key: "N",
      trait: "N",
      traitName: "신경밍",
      image: "/characters/N.png"
    }
  ]

  // trait 코드를 기반으로 BigFive 정보를 가져오기
  const getTraitInfo = (trait: string) => {
    const traitInfo = bigfive.find((item) => item.trait === trait)
    return traitInfo || { traitName: "개방밍", image: "/characters/O.png" }
  }

  return (
    <>
      {user && (
        <>
          <Header title="나의 OCEAN은" />
          <main className="page has-header has-bottom-nav bg-[url(/image/background.gif)] bg-cover bg-center bg-no-repeat">
            <div className="section flex flex-col gap-4 animate-fade-in">
              {/* 환영 메시지 */}
              <LabelCard title={`${user?.nickname}님 안녕하세요!`}>
                <Badge variant="default" className="my-2">
                  <WandSparkles /> {mappingAiStatus(user?.aiStatus)}
                </Badge>
              </LabelCard>

              {/* 오늘의 메시지 */}
              <div>
                <CardSection title="오늘의 메시지">
                  {todayMessage?.trait && (
                    <>
                      <Badge variant="outline">{getTraitInfo(todayMessage.trait).traitName}</Badge>
                      <Image
                        src={getTraitInfo(todayMessage.trait).image}
                        width={0}
                        height={0}
                        sizes="100vw"
                        className="w-1/2 h-1/2 animate-head-shake-loop"
                        alt={getTraitInfo(todayMessage.trait).traitName}
                      />

                      <div className="rounded-lg border border-gray-200 p-4 space-y-4">
                        <Typography type="p" className="text-lg">
                          {todayMessage.message}
                        </Typography>
                      </div>
                    </>
                  )}
                </CardSection>
              </div>

              {/* 일기 작성 */}
              <div>
                <CardSection title="오늘의 일기">
                  <div className="flex items-center justify-center gap-2 mt-[-10px]">
                    <Typography type="p">{formatDate(getTodayDate())} </Typography>
                    <Badge variant={diary ? "default" : "secondary"}>{diary ? "작성 완료" : "작성 전"}</Badge>
                  </div>
                  <DiaryCalendar />
                </CardSection>
              </div>
              {/* 게임 카운트 카드 */}
              {!isPersona && (
                <div>
                  <div>
                    <CardSection title="게임 진행 현황" buttonText="게임 하러가기" buttonOnClick={goGame}>
                      <div className="flex flex-col mb-4">
                        <Typography type="p">페르소나 생성을 위해 각 게임마다 최소 3회 완료해야 해요</Typography>
                        <Typography type="p"> 모든 게임이 완료되면 여러분의 페르소나가 생성됩니다! </Typography>
                        <Typography type="small" className="text-error">
                          {" "}
                          * 게임 최소 조건 충족 후 자정이 지난 뒤 페르소나를 만나볼 수 있어요{" "}
                        </Typography>
                      </div>
                      <div className="flex flex-col gap-4 w-full">
                        {Games.map((game) => (
                          <div
                            key={game.name}
                            className="border-2 border-gray-500 rounded-lg p-4 flex flex-col items-center justify-center gap-4"
                          >
                            <Badge variant={game.count > 3 ? "default" : "secondary"}>
                              {game.count > 3 ? "완료" : "진행중"}
                            </Badge>
                            <Typography type="h4">{game.name}</Typography>
                            <Typography type="p">{game.count}회 진행</Typography>
                          </div>
                        ))}
                      </div>
                    </CardSection>
                  </div>
                </div>
              )}
            </div>
          </main>
          <Navbar />
        </>
      )}
    </>
  )
}
