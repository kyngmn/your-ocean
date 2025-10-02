import { dev } from "@/lib/dev"

interface BigFiveProgressBarProps {
  value: number // 0-100 사이의 값
  indicator: "O" | "C" | "E" | "A" | "N" // Big Five 지표
}

export default function BigFiveProgressBar({ value, indicator }: BigFiveProgressBarProps) {
  // 디버깅용 로그
  dev.log(`BigFiveProgressBar props: ${value}, ${indicator}`)

  // Big Five 지표별 색상 매핑 (직접 색상 사용)
  const colorMap = {
    O: "var(--big-five-O)",
    C: "var(--big-five-C)",
    E: "var(--big-five-E)",
    A: "var(--big-five-A)",
    N: "var(--big-five-N)"
  }

  const colorValue = colorMap[indicator]
  return (
    <div className="w-full h-8 flex border border-gray-300 rounded-lg overflow-hidden">
      {Array.from({ length: 5 }, (_, index) => {
        const quantileValue = (index + 1) * 20 // 20, 40, 60, 80, 100

        // 현재 분위에서 얼마나 채워져야 하는지 계산
        const isFullyActive = value >= quantileValue
        const isPartiallyActive = value > index * 20 && value < quantileValue
        const fillPercentage = isPartiallyActive ? ((value - index * 20) / 20) * 100 : 0

        return (
          <div key={index} className="flex-1 border-r border-gray-300 last:border-r-0 relative">
            {isFullyActive ? (
              <div className="w-full h-full" style={{ backgroundColor: colorValue }} />
            ) : isPartiallyActive ? (
              <div className="w-full h-full bg-gray-100 relative">
                <div
                  className="h-full"
                  style={{
                    width: `${fillPercentage}%`,
                    backgroundColor: colorValue
                  }}
                />
              </div>
            ) : (
              <div className="w-full h-full bg-gray-100" />
            )}
          </div>
        )
      })}
    </div>
  )
}
