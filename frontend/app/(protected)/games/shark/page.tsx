"use client"

import Header from "@/components/layout/Header"
import { Numeric } from "@/types/schema"
import SharkGame from "@/features/games/ui/SharkGame"
import SharkGameGuide from "@/features/games/ui/SharkGameGuide"
import SharkGameResult from "@/features/games/ui/SharkGameResult"
import { useState } from "react"

// 게임 데이터 타입
interface GameData {
  successCount: number
  failCount: number
  noGoCount: number
  averageReactionTime: number
}

export default function SharkPage() {
  type status = "guide" | "game" | "result"

  const [status, setStatus] = useState<status>("guide")
  const [gameData, setGameData] = useState<GameData | null>(null)

  return (
    <>
      <Header type="back" title="상어를 피하자" />
      <main className="page has-header">
        {status === "guide" && (
          <div className="section flex flex-col items-center justify-center min-h-[calc(100vh-120px)]">
            <SharkGameGuide onStartGame={() => setStatus("game")} />
          </div>
        )}

        {status === "game" && (
          <div className="h-full">
            <SharkGame
              onGameEnd={(data: GameData) => {
                setGameData(data)
                setStatus("result")
              }}
            />
          </div>
        )}

        {status === "result" && gameData && (
          <div className="section flex flex-col items-center justify-center min-h-[calc(100vh-130px)]">
            <SharkGameResult
              totalCorrectCnt={gameData.successCount}
              totalIncorrectCnt={gameData.failCount}
              avgReactionTime={gameData.averageReactionTime as unknown as Numeric}
              nogoIncorrectCnt={gameData.noGoCount}
            />
          </div>
        )}
      </main>
    </>
  )
}
