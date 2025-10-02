import GameResult from "./GameResult"
import { useRouter } from "next/navigation"

interface TreasureGameResultProps {
  successRate: number
  acceptRate: number
}

export default function TreasureGameResult({
  successRate,
  acceptRate,
}: TreasureGameResultProps) {
  const router = useRouter()
  const goGameMain = () => {
    router.push("/games")
  }

  const ResultItem = [
    {
      resultTitle: "제안 성공률",
      result: `${Math.round(successRate)}%`
    },
    {
      resultTitle: "제안 수락률",
      result: `${Math.round(acceptRate)}%`
    },
  ]

  return (
    <>
      <div className="w-full">
        <GameResult results={ResultItem} buttonText="확인" buttonOnClick={goGameMain} />
      </div>
    </>
  )
}
