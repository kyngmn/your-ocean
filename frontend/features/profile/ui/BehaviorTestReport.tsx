import CardSection from "@/components/common/CardSection"
import DefaultBadge from "@/components/common/DefaultBadge"
import LabelCard from "@/components/common/LabelCard"
import Typography from "@/components/ui/Typography"
import CompareChart from "./CompareChart"
import Image from "next/image"
import { Badge } from "@/components/ui/badge"
import { BigFiveScores, useReportStore } from "@/stores/report"
import { dev } from "@/lib/dev"
import { useMemo } from "react"
import { useFinalReport } from "@/features/profile/queries"
import Loading from "@/components/common/Loading"
import { Label } from "@/components/ui/label"

interface FinalReport {
  bigFiveScores: BigFiveScores
  headline: string
  insights: {
    gap: string
    main: string
    strength: string
  }
}

export default function BehaviorTestReport() {
  const { selfReportBigFive } = useReportStore()
  const { data: finalReport } = useFinalReport()

  // 안전하게 JSON 파싱 (문자열이면 parse, 객체면 그대로 사용) - 메모이제이션
  const parsedFinalReport = useMemo(() => {
    return finalReport?.content
      ? ((typeof finalReport.content === "string"
          ? JSON.parse(finalReport.content)
          : finalReport.content) as FinalReport)
      : null
  }, [finalReport?.content])

  // 데이터 준비 전 가드
  if (!parsedFinalReport) return <Loading />

  // finalReportBigFive에서 가장 높은 BigFive 수치 찾기
  const maxTrait = Object.entries(parsedFinalReport?.bigFiveScores as BigFiveScores).reduce(
    (max, [trait, value]) => ((value as number) > max.value ? { trait, value: value as number } : max),
    { trait: "O", value: 0 }
  )

  // finalReportBigFive 저장
  const finalReportBigFive = parsedFinalReport?.bigFiveScores

  dev.log("finalReportBigFive", finalReportBigFive)
  dev.log("selfReportBigFive", selfReportBigFive)

  // BigFive 코드에 따른 캐릭터 매칭
  const getCharacterInfo = (trait: string) => {
    const characterMap = {
      O: { name: "개방밍", image: "/characters/O.png" },
      C: { name: "성실밍", image: "/characters/C.png" },
      E: { name: "활발밍", image: "/characters/E.png" },
      A: { name: "친화밍", image: "/characters/A.png" },
      N: { name: "신경밍", image: "/characters/N.png" }
    }
    return characterMap[trait as keyof typeof characterMap] || { name: "개방밍", image: "/characters/O.png" }
  }

  const characterInfo = getCharacterInfo(maxTrait.trait)

  // 일치도 계산 함수
  const calculateMatchPercentage = (self: BigFiveScores | null, final: BigFiveScores | null): number => {
    if (!self || !final) return 0

    const traits = ["O", "C", "E", "A", "N"] as const
    let totalDiff = 0

    traits.forEach((trait) => {
      const selfValue = self[trait]
      const finalValue = final[trait]
      const diff = Math.abs(selfValue - finalValue)
      totalDiff += diff
    })

    // 최대 차이값 (각 특성당 최대 100)
    const maxTotalDiff = traits.length * 100

    // 일치도 = (1 - (총 차이 / 최대 차이)) * 100
    const matchPercentage = Math.round((1 - totalDiff / maxTotalDiff) * 100)

    return Math.max(0, matchPercentage) // 최소 0%
  }

  const matchPercentage = calculateMatchPercentage(selfReportBigFive, finalReportBigFive)

  return (
    <>
      <section className="section space-y-4">

        {/* 업데이트일 */}
        <div>
        <LabelCard title={`업데이트일: ${finalReport?.createdAt.split("T")[0]}`} />
        </div>
      
        {/* 대표캐릭터 */}
        <div>
          <div className="text-center mt-8 mb-0">
            <Badge variant="outline">대표캐릭터</Badge>
            <Typography type="h4">{characterInfo.name}</Typography>
          </div>
          <div className="flex justify-center w-full">
            <Image src={characterInfo.image} alt={maxTrait.trait} width={300} height={300} />
          </div>
          <div className="text-center">
            <Typography type="h3">BIG5 종합 결과</Typography>
            <div></div>
            <DefaultBadge variant="outline" text={`일치도 ${matchPercentage}%`} />
            <div className="space-y-2 mb-2">
              <Typography type="p">자기보고식과 행동 분석의 일치도를 비교합니다.</Typography>
              <Typography type="p">
                행동 데이터는 <b>일기, 게임, 채팅</b>을 기반으로 분석했어요.
              </Typography>
            </div>
          </div>

          {/* 자기보고식과 행동 분석 비교 */}
          <div>
            <CardSection title="">
              <CompareChart report1={selfReportBigFive} report2={finalReportBigFive} />
            </CardSection>
          </div>

          {/* 행동 데이터 분석 보고서 */}
          <div className="mt-8">
            <CardSection title="행동 데이터 분석 결과">
              <div className="space-y-2 ">
                <Typography type="h4" className="mb-8">
                  {parsedFinalReport?.headline}
                </Typography>

                <div className="flex flex-col gap-4">
                  <div className="w-full">
                    <Label className="text-left block mb-2 font-bold border-b border-gray-300 pb-2">
                      성격 대비 분석
                    </Label>
                    <Typography type="p" className="text-left">
                      {parsedFinalReport?.insights.gap}
                    </Typography>
                  </div>
                  <div className="w-full">
                    <Label className="text-left block mb-2 font-bold border-b border-gray-300 pb-2">
                      전체적인 성격
                    </Label>
                    <Typography type="p" className="text-left">
                      {parsedFinalReport?.insights.main}
                    </Typography>
                  </div>
                  <div className="w-full">
                    <Label className="text-left block mb-2 font-bold border-b border-gray-300 pb-2">강점</Label>
                    <Typography type="p" className="text-left">
                      {parsedFinalReport?.insights.strength}
                    </Typography>
                  </div>
                </div>
              </div>
            </CardSection>
          </div>
        </div>
      </section>
    </>
  )
}
