import DiaryCalendarDialog from "@/features/diaries/ui/DiaryCalendarDialog"
import DiaryContent from "@/features/diaries/ui/DiaryContent"
import Header from "@/components/layout/Header"
import Navbar from "@/components/layout/Navbar"
import { getDiaryByDate } from "@/actions/diaries"
import { getTodayDate } from "@/lib/date"

export const dynamic = "force-dynamic"

export default async function DiaryMainPage() {
  const today = getTodayDate()
  const data = await getDiaryByDate(today)
  const diary = data.result
  return (
    <>
      <Header title="일기" rightSlot={<DiaryCalendarDialog />} />

      <main className="page px-0 sm:px-4 has-header has-bottom-nav">
        <section className="py-4">
          <DiaryContent initialDate={today} initialDiary={diary} />
        </section>
      </main>
      <Navbar />
    </>
  )
}
