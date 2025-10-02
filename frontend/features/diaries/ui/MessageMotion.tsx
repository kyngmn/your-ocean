"use client"

import { memo } from "react"
import { motion } from "framer-motion"

const MessageMotion = ({ chars }: { chars: string[] }) => {
  if (!Array.isArray(chars)) return
  return (
    <motion.span className="text-sm font-mono">
      {chars.map((char, index) => (
        <motion.span key={index} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: index * 0.05 }}>
          {char}
        </motion.span>
      ))}
    </motion.span>
  )
}
export default memo(MessageMotion)
