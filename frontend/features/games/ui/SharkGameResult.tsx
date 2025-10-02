import {GameGngResults } from "@/types/schema"
import GameResult from "./GameResult"
import { useRouter } from "next/navigation"

interface SharkGameResultProps extends Partial<GameGngResults> {
  nogoIncorrectCnt?: number
}

export default function SharkGameResult({
  totalCorrectCnt,
  totalIncorrectCnt,
  avgReactionTime,
  nogoIncorrectCnt
}: SharkGameResultProps) {
  const router = useRouter()
  const goGameMain = () => {
    router.push("/games")
  }

  const ResultItem = [
    {
      resultTitle: "성공한 횟수",
      result: `${totalCorrectCnt}회`
    },
    {
      resultTitle: "실패한 횟수",
      result: `${totalIncorrectCnt}회`
    },
    {
      resultTitle: "놓친 횟수",
      result: `${nogoIncorrectCnt}회`
    },
    {
      resultTitle: "평균 반응 속도",
      result: `${(Number(avgReactionTime || 0) / 1000).toFixed(1)}초`
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
