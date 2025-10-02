"use client"

import ListItem from "@/components/common/ListItem"
import FriendListItem from "@/features/communities/ui/FriendListItem"
import { useGetFriends } from "@/features/communities/queries"
import Loading from "@/components/common/Loading"
import Typography from "@/components/ui/Typography"
import Image from "next/image"
import { Button } from "@/components/ui/button"
import { useRouter } from "next/navigation"
import { dev } from "@/lib/dev"
import { useState, useEffect } from "react"

export default function FriendsList() {
  const { data: friends, isLoading, error, refetch } = useGetFriends()
  const router = useRouter()
  const [showLoading, setShowLoading] = useState(true)
  
  dev.log("🔥 FriendList 렌더링:", { friends, isLoading, error })

  // 1초 후에 로딩 상태 해제
  useEffect(() => {
    const timer = setTimeout(() => {
      setShowLoading(false)
    }, 1000)

    return () => clearTimeout(timer)
  }, [])

  if (isLoading || showLoading) {
    return <Loading />
  }

  if (error) {
    throw error
  }

  const goProfile = () => {
    router.push("/profile")
  }

  if (!friends || friends.length === 0) {
    return (
      <>
        <div className="flex items-center justify-center min-h-[calc(90vh-var(--header-height)-var(--bottom-nav-height))]">
            <div className="flex flex-col items-center justify-center gap-4">
              <Image src="/image/error.png" alt="shark" width={200} height={200} />
              <Typography type="h4">아직 친구가 없어요!</Typography>
              <Typography type="h4">친구 추가 링크를 통해 친구를 추가해보세요.</Typography>
              <Button size="long" onClick={goProfile}>
                친구 추가 하러가기
              </Button>
          </div>
        </div>
      </>
    )
  }

  return (
    <>
      <ListItem
        direction="col"
        className="gap-4"
        items={friends.map((friend) => (
          <FriendListItem
            key={friend.id}
            friendId={friend.friendId}
            friendNickname={friend.friendNickname}
            friendProfileImageUrl={friend.friendProfileImageUrl}
            hasPersona={friend.hasPersona}
          />
        ))}
      />
    </>
  )
}
