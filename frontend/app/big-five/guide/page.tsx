import GuideTab from "@/features/big-five/ui/GuideTab"
import Header from "@/components/layout/Header"

export default function GuidePage() {
  return (
    <>
      <Header type="back" />
      <main className="page has-header">
        <GuideTab />
      </main>
    </>
  )
}
