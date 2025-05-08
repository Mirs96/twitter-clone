import React, { useState, useEffect, useCallback } from 'react';
import SingleReply from '../SingleReply/SingleReply';
import { ReplyDetails } from '../../../types/reply/replyDetails';
import styles from './NestedReply.module.css'; 
import { getNestedRepliesByParentReplyId } from '../../../services/replyService';

interface NestedReplyProps {
  parentReplyId: number;
  onOpenReplyPopup: (replyId: number) => void;
}

const NestedReply: React.FC<NestedReplyProps> = ({ parentReplyId, onOpenReplyPopup }) => {
  const [nestedReplies, setNestedReplies] = useState<ReplyDetails[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadNestedReplies = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const replies = await getNestedRepliesByParentReplyId(parentReplyId);
      
      const repliesWithState = replies.map(r => ({ ...r, showNested: false }));
      setNestedReplies(repliesWithState);
    } catch (err) {
      console.error('Failed to load nested replies:', err);
      setError('Could not load replies.');
    } finally {
      setIsLoading(false);
    }
  }, [parentReplyId]);

  useEffect(() => {
    loadNestedReplies();
  }, [loadNestedReplies]); 

  const toggleNestedReplies = (replyId: number) => {
    setNestedReplies(prevReplies => 
        prevReplies.map(reply => 
            reply.id === replyId ? { ...reply, showNested: !reply.showNested } : reply
        )
    );
  };

  if (isLoading) {
    return <div className={styles.loading}>Loading replies...</div>;
  }

  if (error) {
    return <div className={styles.error}>{error}</div>;
  }

  return (
    <div className={styles.nestedRepliesContainer}> 
      {nestedReplies.map((nestedReply) => (
        <div key={nestedReply.id}>
          <SingleReply 
            reply={nestedReply} 
            onOpenReplyPopup={onOpenReplyPopup}
            onToggleNested={nestedReply.hasNestedReplies ? () => toggleNestedReplies(nestedReply.id) : undefined}
            isNestedVisible={!!nestedReply.showNested}
          />
          {nestedReply.showNested && (
             <div className={styles.deeplyNested}> 
                 <NestedReply 
                    parentReplyId={nestedReply.id} 
                    onOpenReplyPopup={onOpenReplyPopup} 
                 />
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default NestedReply;
