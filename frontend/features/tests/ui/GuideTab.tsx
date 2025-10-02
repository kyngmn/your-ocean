"use client"

import { ClipboardClock, MousePointerClick, Snail } from "lucide-react"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import Image from "next/image"
import Typography from "@/components/ui/Typography"
import { useRouter } from "next/navigation"

export default function GuideTab() {
  const router = useRouter()

  const goProgress = () => {
    router.push("/big-five/progress")
  }

  return (
    <div className="section flex flex-1 flex-col justify-center ">
      {/* BIG5 - 오른쪽 상단 */}
      <div className="flex flex-1/2 flex-col justify-center items-end text-right gap-4">
        <Badge variant="outline" className="rounded-full border-black p-3 min-w-20">
          <Typography type="h4">BIG5</Typography>
        </Badge>
        <Image src="/image/bigfive_guide.png" alt="BIG5" width={300} height={300} />
        <Typography type="p" className="font-bold">
          심리학에서 가장 신뢰받고 있는 검사에요
        </Typography>
        <Typography type="p" className="font-bold">
          성격을 다섯 가지 큰 요인으로 나눠서 측정해요
        </Typography>
      </div>

      {/* TIP - BIG5 아래 영역, 왼쪽 */}
      <div className="flex flex-1/2 flex-col items-start text-left gap-4">
        <Badge variant="outline" className="rounded-full border-black p-3 min-w-20 mb-2">
          <Typography type="h4">TIP</Typography>
        </Badge>
        <div className="flex items-center gap-4">
          <ClipboardClock />
          <Typography type="p" className="font-bold">
            총 120 문항, 약 10분 정도 걸려요
          </Typography>
        </div>
        <div className="flex items-center gap-4">
          <Snail />
          <Typography type="p" className="font-bold">
            중간에 멈추지 말고 한 번에 해주세요
          </Typography>
        </div>
        <div className="flex items-center gap-4">
          <MousePointerClick />
          <Typography type="p" className="font-bold">
            떠오르는 대로 응답해주세요
          </Typography>
        </div>
      </div>

      <div className="flex justify-end">
        <Button variant="default" size="lg" onClick={goProgress}>
          다음
        </Button>
      </div>
    </div>
  )
}
