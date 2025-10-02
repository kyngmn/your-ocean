import { acceptInvite } from "@/app/actions/friends"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import Typography from "@/components/ui/Typography"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { toast } from "sonner"
import { useQueryClient } from "@tanstack/react-query"

interface FriendInviteNewProps {
  nickname: string
  profileImage: string
  token: string
}

export default function FriendInviteNew({ nickname, profileImage, token }: FriendInviteNewProps) {
  const router = useRouter()
  const queryClient = useQueryClient()
  const [isLoading, setIsLoading] = useState(false)

  // 친구 수락
  const handleAccept = async () => {
    if (!token) return

    setIsLoading(true)
    try {
      const result = await acceptInvite(token)

      if (result.data.isSuccess) {
        toast.success("친구 요청이 수락되었습니다!")
        
        // 캐시 무효화하여 새로 데이터 불러오기
        await queryClient.invalidateQueries({ queryKey: ["friends"] })
        
        setTimeout(() => {
          router.push("/communities")
        }, 1000)

        
      } else {
        toast.error("이미 만료된 링크이니 새로운 링크로 접근해주세요.")
      }
    } catch (error) {
      toast.error("친구 요청 수락에 실패했습니다.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <>
      <div className="section flex-1 flex flex-col items-center justify-center gap-4">
        {/* 프로필 사진 */}
        <Avatar className="w-40 h-40">
          <AvatarImage src={profileImage || "/image/default_profile.png"} />
          <AvatarFallback>Profile</AvatarFallback>
        </Avatar>
        {/* 친구 수락 안내 */}
        <div>
          <div className="flex flex-col items-center justify-center gap-4">
            <Typography type="h3">
              <span className="text-accent-blue">{nickname}</span> 님과 친구를 맺습니다.
            </Typography>
            <div className="flex flex-col items-center justify-center gap-2">
              <Typography type="p">친구를 맺으면 친구 목록에서 확인 가능 합니다.</Typography>
              <Typography type="p">이후 친구도 나를 닮은 밍과 대화할 수 있어요!</Typography>
            </div>
            <div className="flex items-center justify-center gap-2">
              <Button size="lg" className="w-full" onClick={handleAccept} disabled={isLoading}>
                {isLoading ? "처리 중" : "수락"}
              </Button>
              {/* <Button 
                  size="lg" 
                  variant="secondary" 
                  className="w-full"
                  onClick={handleDecline}
                  disabled={isLoading}
                >
                  거절
                </Button> */}
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
