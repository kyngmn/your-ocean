"use client"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { redirect, useRouter } from "next/navigation"

import { Button } from "@/components/ui/button"
import { DialogTrigger } from "@/components/ui/dialog"
import Header from "@/components/layout/Header"
import ProfileEditDialog from "@/features/profile/ui/ProfileUpdateDialog"
import ShareLinkDialog from "@/features/profile/ui/ShareLinkDialog"
import SignOutDialog from "@/features/profile/ui/SignOutDialog"
import Typography from "@/components/ui/Typography"
import { useAuthStore } from "@/stores/auth-store"
import { ChevronRight } from "lucide-react"
import { Skeleton } from "@/components/ui/skeleton"

export default function ProfilePage() {
  const router = useRouter()
  const { user, logout } = useAuthStore()

  // 나의 페르소나 페이지로 이동
  const goPersona = () => {
    router.push("/profile/persona")
  }

  // 리포트 페이지로 이동
  const goReport = () => {
    router.push("/profile/report")
  }

  // 친구 목록 페이지로 이동
  const goFriends = () => {
    router.push("/communities")
  }

  // 로그아웃
  const handleLogout = async () => {
    const res = await logout()
    if (res?.isSuccess) {
      redirect("/login")
    }
  }

  return (
    <>
      <Header title="프로필" />
      <main className="page has-header has-bottom-nav">
        {/* 메인영역 */}
        <div className="section flex flex-col items-center gap-4">
          {/* 프로필 사진 */}
          <Avatar className="w-40 h-40">
            <AvatarImage src={user?.profileImageUrl || "/image/default_profile.png"} />
            <AvatarFallback><Skeleton bold /></AvatarFallback>
          </Avatar>

          {/* 닉네임 */}
          {user ? (
          <Typography type="h4">{user.nickname}</Typography>
          ) : (
            <Skeleton bold className="w-20 h-6" />
          )}

          {/* 버튼 영역 */}
          <div className="flex flex-col gap-2 w-full mt-8">
            {/* ======================= 내 정보 ======================= */}
            <div className="flex flex-col gap-2 border rounded-lg shadow-sm border-gray-100 p-4">
              <Typography type="h3">내 정보</Typography>
              {/* 프로필 수정 */}
              <ProfileEditDialog nickname={user?.nickname} profileImageUrl={user?.profileImageUrl}>
                <DialogTrigger asChild>
                  <Button size="long" variant="ghost" className="px-4">
                    <div className="flex items-center justify-between w-full">
                      <Typography type="h4">프로필 수정</Typography>
                      <ChevronRight />
                    </div>
                  </Button>
                </DialogTrigger>
              </ProfileEditDialog>

              {/* 리포트 보기 */}
              <Button size="long" variant="ghost" className="px-4" onClick={goReport}>
                <div className="flex items-center justify-between w-full">
                  <Typography type="h4">리포트 보기</Typography>
                  <ChevronRight />
                </div>
              </Button>

              {/* 나의 페르소나 */}
              {user?.aiStatus === "GENERATED" && (
                <Button size="long" variant="ghost" className="px-4" onClick={goPersona}>
                  <div className="flex items-center justify-between w-full">
                    <Typography type="h4">나의 페르소나</Typography>
                    <ChevronRight />
                  </div>
                </Button>
              )}
            </div>

            {/* ======================= 친구 ======================= */}
            <div className="flex flex-col gap-2 border rounded-lg shadow-sm border-gray-100 p-4">
              <Typography type="h3">친구</Typography>
              {/* 친구 추가 링크 */}
              <ShareLinkDialog>
                <DialogTrigger asChild>
                  <Button size="long" variant="ghost" className="px-4">
                    <div className="flex items-center justify-between w-full">
                      <Typography type="h4">친구 추가 링크</Typography>
                      <ChevronRight />
                    </div>
                  </Button>
                </DialogTrigger>
              </ShareLinkDialog>

              {/* 친구 목록 */}
              <Button size="long" variant="ghost" className="px-4" onClick={goFriends}>
                <div className="flex items-center justify-between w-full">
                  <Typography type="h4">친구 목록</Typography>
                  <ChevronRight />
                </div>
              </Button>
            </div>

            {/* ======================= 회원 ======================= */}
            <div className="flex flex-col gap-2 border rounded-lg shadow-sm border-gray-100 p-4">
              {/* 로그아웃 */}
              <Button
                size="long"
                variant="ghost"
                className="px-4 border-destructive text-destructive hover:text-destructive "
                onClick={handleLogout}
              >
                <div className="flex items-center justify-between w-full">
                  <Typography type="h4">로그아웃</Typography>
                  <ChevronRight />
                </div>
              </Button>

              {/* 회원 탈퇴 */}
              <SignOutDialog>
                <DialogTrigger asChild>
                  <Button
                    size="long"
                    variant="ghost"
                    className="px-4 border-destructive text-destructive hover:text-destructive"
                  >
                    <div className="flex items-center justify-between w-full">
                      <Typography type="h4">회원 탈퇴</Typography>
                      <ChevronRight />
                    </div>
                  </Button>
                </DialogTrigger>
              </SignOutDialog>
            </div>
          </div>
        </div>
      </main>
    </>
  )
}
