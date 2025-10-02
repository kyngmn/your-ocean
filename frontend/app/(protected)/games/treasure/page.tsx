"use client"

import Header from "@/components/layout/Header"
import { useState } from "react"
import TreasureGameGuide from "@/features/games/ui/TreasureGameGuide"
import TreasureGame from "@/features/games/ui/TreasureGame"
import TreasureGameResult from "@/features/games/ui/TreasureGameResult"

// 게임 데이터 타입
interface GameData  {
  successRate: number, //성사률
  acceptRate: number,  //수락률
}

  export default function SharkPage() {
  type status = "guide" | "game" | "result"

  const [status, setStatus] = useState<status>("guide")
  const [gameData, setGameData] = useState<GameData | null>(null)

  return (
    <>
      <Header type="back" title="보물 나누기" />
      <main className="page has-header">
        {status === "guide" && (
          <div className="section flex flex-col items-center justify-center min-h-[calc(100vh-120px)]">
            <TreasureGameGuide onStartGame={() => setStatus("game")} />
          </div>
        )}

        {status === "game" && (
          <div className="h-full">
            <TreasureGame
              onGameEnd={(data: GameData) => {
                setGameData(data)
                setStatus("result")
              }}
            />
          </div>
        )}

        {status === "result" && gameData && (
          <div className="section flex flex-col items-center justify-center min-h-[calc(100vh-130px)]">
            <TreasureGameResult
              acceptRate={gameData.acceptRate}
              successRate={gameData.successRate}
            />
          </div>
        )}
      </main>
    </>
  )
}
