"use client"

import { CircleUserRound, House, Joystick, MessageCircleMore, NotebookPen } from "lucide-react"
import { usePathname, useRouter } from "next/navigation"

import Typography from "../ui/Typography"

export default function Navbar() {
  const router = useRouter()
  const pathname = usePathname()

  const goProfile = () => {
    router.push("/profile")
  }
  const goGame = () => {
    router.push("/games")
  }
  const goHome = () => {
    router.push("/")
  }
  const goChat = () => {
    router.push("/chats")
  }
  const goDiary = () => {
    router.push("/diaries")
  }

  // 현재 경로에 따른 활성 탭 확인
  const isActive = (path: string) => {
    if (path === "/") {
      return pathname === "/"
    }
    return pathname.startsWith(path)
  }

  return (
    <div className="bottom-nav">
      <div className="bg-white border-t border-gray-100 z-50">
        <div className="flex justify-around items-center px-4 border-t border-gray-50">
          <div
            className={`flex flex-col items-center gap-1 py-2 cursor-pointer transition-colors ${
              isActive("/") ? "" : "text-gray-400"
            }`}
            onClick={goHome}
          >
            <House className={`w-6 h-6 ${isActive("/") ? "" : "text-gray-400"}`} />
            <Typography type="pale" className={isActive("/") ? "font-semibold" : "text-gray-400"}>
              홈
            </Typography>
          </div>

          <div
            className={`flex flex-col items-center gap-1 py-2 cursor-pointer transition-colors ${
              isActive("/games") ? "" : "text-gray-400"
            }`}
            onClick={goGame}
          >
            <Joystick className={`w-6 h-6 ${isActive("/games") ? "" : "text-gray-400"}`} />
            <Typography type="pale" className={isActive("/games") ? "font-semibold" : "text-gray-400"}>
              게임
            </Typography>
          </div>
          <div
            className={`flex flex-col items-center gap-1 py-2 cursor-pointer transition-colors ${
              isActive("/chats") ? "" : "text-gray-400"
            }`}
            onClick={goChat}
          >
            <MessageCircleMore className={`w-6 h-6 ${isActive("/chats") ? "" : "text-gray-400"}`} />
            <Typography type="pale" className={isActive("/chats") ? "font-semibold" : "text-gray-400"}>
              채팅
            </Typography>
          </div>
          <div
            className={`flex flex-col items-center gap-1 py-2 cursor-pointer transition-colors ${
              isActive("/diaries") ? "" : "text-gray-400"
            }`}
            onClick={goDiary}
          >
            <NotebookPen className={`w-6 h-6 ${isActive("/diaries") ? "" : "text-gray-400"}`} />
            <Typography type="pale" className={isActive("/diaries") ? "font-semibold" : "text-gray-400"}>
              일기
            </Typography>
          </div>
          <div
            className={`flex flex-col items-center gap-1 py-2 cursor-pointer transition-colors ${
              isActive("/profile") ? "" : "text-gray-400"
            }`}
            onClick={goProfile}
          >
            <CircleUserRound className={`w-6 h-6 ${isActive("/profile") ? "" : "text-gray-400"}`} />
            <Typography type="pale" className={isActive("/profile") ? "font-semibold" : "text-gray-400"}>
              프로필
            </Typography>
          </div>
        </div>
      </div>
    </div>
  )
}
