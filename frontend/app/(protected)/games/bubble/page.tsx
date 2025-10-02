"use client"

import Header from "@/components/layout/Header"
import BubbleGame from "@/features/games/ui/BubbleGame"
import BubbleGameGuide from "@/features/games/ui/BubbleGameGuide"
import BubbleGameResult from "@/features/games/ui/BubbleGameResult"
import { useState } from "react"


// 컴포넌트 상태
type status = "guide" | "game" | "result"

// 게임 결과 데이터 
interface GameData {
  successBalloons: number
  failBalloons: number
  rewardAmount: number
  missedReward: number
}

export default function BubblePage() {
  const [status, setStatus] = useState<status>("guide")
  const [gameData, setGameData] = useState<GameData | null>(null)
    
  return (
    <>
      <Header type="back" title="버블 게임" />
      <main className="page has-header">
        {status === "guide" && (
          <div className="section flex flex-col items-center justify-center min-h-[calc(100vh-120px)]">
            <BubbleGameGuide onStartGame={() => setStatus("game")} />
          </div>
        )}
        
        {status === "game" && (
          <div className="h-full">
            <BubbleGame onGameEnd={(data) => {
              setGameData(data)
              setStatus("result")
            }} />
          </div>
        )}
        
        {status === "result" && gameData && (
          <div className="section flex flex-col items-center justify-center min-h-[calc(100vh-130px)]">
            <BubbleGameResult 
              successBalloons={gameData.successBalloons} 
              failBalloons={gameData.failBalloons} 
              rewardAmount={gameData.rewardAmount} 
              missedReward={gameData.missedReward}
            />
          </div>
        )}
      </main>
    </>
  )
}
