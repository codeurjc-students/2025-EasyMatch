export interface ChatMessage {
  matchId: number;
  content: string;
  senderUsername: string;
  type: string;
  timestamp: string;
}

export interface ChatGroup {
  matchId: number;
  lastMessage: ChatMessage;
  messages: ChatMessage[];
}