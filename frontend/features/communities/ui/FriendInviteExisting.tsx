import { Button } from "@/components/ui/button"
import Typography from "@/components/ui/Typography"
import Image from "next/image"
import { useRouter } from "next/navigation"

export default function FriendInviteExisting() {


  const router = useRouter()

  const goCommunity = () => {
    router.push("/communities")
  }
  return (
    <>
      <div className="section flex-1 flex flex-col items-center justify-center gap-4">
        <Image src="/characters/friend.png" alt="friend_existing" className="animate-bounce" width={200} height={200} />
        <Typography type="h3">이미 친구로 맺어져 있어요!</Typography>
        <Typography type="p">친구 목록에서 확인 가능해요</Typography>
        <Button size="lg" className="w-full" onClick={goCommunity}>
          확인
        </Button>
      </div>
    </>
  )
}
