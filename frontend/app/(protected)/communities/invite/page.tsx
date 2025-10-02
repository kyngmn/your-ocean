"use client"

import { useSearchParams, useRouter } from "next/navigation"
import { useState, useEffect } from "react"
import { dev } from "@/lib/dev"
import Loading from "@/components/common/Loading"
import FriendInviteNew from "@/features/communities/ui/FriendInviteNew"
import FriendInviteExisting from "@/features/communities/ui/FriendInviteExisting"
import { useQueryClient } from "@tanstack/react-query"
import { useGetFriends } from "@/features/communities/queries"
import { FriendResponseDTO } from "@/types/dto"

export default function InvitePage() {
  const searchParams = useSearchParams()
  const router = useRouter()
  const [token, setToken] = useState<string | null>(null)
  const [nickname, setNickname] = useState("")
  const [profileImage, setProfileImage] = useState("")

  // 친구 정보 불러오기
  const queryClient = useQueryClient()
  const cachedFriends = queryClient.getQueryData(["friends"])
  
  const { data: fetchedFriends } = useGetFriends({
    enabled: !cachedFriends
  })
  
  const friends = cachedFriends || fetchedFriends
  
  // 친구 여부 확인
  const isFriend = Array.isArray(friends) && friends.some((friend: FriendResponseDTO) => 
    friend.friendNickname === nickname
  )


  useEffect(() => {
    // 친구 정보를 URL 파라미터에서 가져오기
    const nickname = searchParams.get("nickname")
    const profileImage = searchParams.get("profileImage")
    setNickname(nickname || "")
    setProfileImage(profileImage || "")

    // 쿠키에서 토큰 확인 (새로운 방식)
    const cookieToken = document.cookie
      .split("; ")
      .find((row) => row.startsWith("inviteToken="))
      ?.split("=")[1]

    if (cookieToken) {
      setToken(cookieToken)
      dev.log("🔥 초대 토큰:", cookieToken, { cookieToken })

      // 쿠키에서 가져온 경우 쿠키 삭제
      if (cookieToken) {
        document.cookie = "inviteToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
      }
    } else {
      // 토큰이 없으면 에러 페이지로 리다이렉트
      throw new Error("초대 토큰이 없습니다.")
    }
  }, [searchParams, router])

  // 로딩 표시
  if (!token) {
    return <Loading />
  }


  
  return (
    <>
      <main className="page">
        {isFriend ? <FriendInviteExisting /> : <FriendInviteNew nickname={nickname} profileImage={profileImage} token={token} />}
      </main>
    </>
  )
}
