import { GameBartResults } from "@/types/schema"
import GameResult from "./GameResult"
import { useRouter } from "next/navigation"

export default function BubbleGameResult({
  successBalloons,
  failBalloons,
  rewardAmount,
  missedReward
}: Partial<GameBartResults>) {
  const router = useRouter()
  const goGameMain = () => {
    router.push("/games")
  }

  const ResultItem = [
    {
      resultTitle: "성공한 횟수",
      result: `${successBalloons}회`
    },
    {
      resultTitle: "터진 횟수",
      result: `${failBalloons}회`
    },
    {
      resultTitle: "획득 점수",
      result: `${rewardAmount}점`
    },
    {
      resultTitle: "놓친 점수",
      result: `${missedReward}점`
    }
  ]

  return (
    <>
      <div className="w-full">
        <GameResult results={ResultItem} buttonText="확인" buttonOnClick={goGameMain} />
      </div>
    </>
  )
}
