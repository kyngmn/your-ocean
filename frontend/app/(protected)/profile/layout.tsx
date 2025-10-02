import Navbar from "@/components/layout/Navbar"

export default function ProfileLayout({ children }: { children: React.ReactNode }) {
  return (
    <>
      {children}
        <Navbar/>
    </>
  )
}
