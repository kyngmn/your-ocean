import { withAuth } from "@/components/common/withAuth"

function ProtectedLayout({ children }: { children: React.ReactNode }) {
  return <>{children}</>
}
export default withAuth(ProtectedLayout)
