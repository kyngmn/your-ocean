import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import Typography from "@/components/ui/Typography"
import { CircleX, MessageCircleMore } from "lucide-react"
import DeleteDialog from "./DeleteDialog"
import { DialogTrigger } from "@radix-ui/react-dialog"
import { FriendResponseDTO } from "@/types/dto"


const defaultProfileImageUrl = "/image/default_profile.png"

export default function FriendListItem({ friendId, friendNickname, friendProfileImageUrl, hasPersona }: Partial<FriendResponseDTO>

) {
  
  return (
    <>
    <div className="flex items-center gap-3 w-full">
      {/* 프로필 영역 (왼쪽) */}
      <div className="flex items-center gap-3 flex-1 min-w-0">
        {/* 아바타 */}
        <Avatar className="w-10 h-10 flex-shrink-0">
          <AvatarImage src={friendProfileImageUrl || defaultProfileImageUrl} alt="Persona" />
          <AvatarFallback>
            <AvatarImage src="/characters/random.png" alt="Persona" />
          </AvatarFallback>
        </Avatar>

        {/* 닉네임 */}
        <div className="flex-1 min-w-0">
          <Typography type="large" className="font-medium truncate">
            {friendNickname}
          </Typography>
        </div>
      </div>

      {/* 아이콘 영역 (오른쪽) */}
      <div className="flex items-center gap-2 flex-shrink-0">
        {hasPersona && <MessageCircleMore className="w-8 h-8 text-gray-500" />}
        <DeleteDialog friendId={friendId || 0}>
          <DialogTrigger asChild>
            <button className="p-1 hover:bg-gray-100 rounded">
              <CircleX className="w-8 h-8 text-gray-500" />
            </button>
          </DialogTrigger>
        </DeleteDialog>
      </div>
    </div>
    </>
  )
}
