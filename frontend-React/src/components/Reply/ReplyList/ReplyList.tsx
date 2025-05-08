import React, { useState, useEffect, useRef, useCallback } from 'react';
import SingleReply from '../SingleReply/SingleReply';
import NestedReply from '../NestedReply/NestedReply';
import { ReplyDetails } from '../../../types/reply/replyDetails';
import styles from './ReplyList.module.css';
import { getMainRepliesByTweetId, getRepliesByUserId } from '../../../services/replyService';

interface ReplyListProps {
  tweetId?: number;
  userId?: number; 
  newReply?: ReplyDetails | null;
  listKey: string;
  onOpenReplyPopup: (replyId: number) => void;
}

const ReplyList: React.FC<ReplyListProps> = ({ tweetId, userId, newReply, onOpenReplyPopup }) => {
  const [mainReplies, setMainReplies] = useState<ReplyDetails[]>([]);
  const [page, setPage] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [hasMoreReplies, setHasMoreReplies] = useState(true);
  const containerRef = useRef<HTMLDivElement>(null);
  const size = 10;

  const loadReplies = useCallback(async (currentPage: number) => {
    if (isLoading || !hasMoreReplies) return;
    setIsLoading(true);

    try {
      let response;
      if (tweetId !== undefined) {
        response = await getMainRepliesByTweetId(tweetId, currentPage, size);
         response.content.forEach(reply => {
             if (reply.hasNestedReplies === undefined) reply.hasNestedReplies = false; // Initialize if missing
             reply.showNested = false; // Always default to hidden
         });
      } else if (userId !== undefined) {
        response = await getRepliesByUserId(userId, currentPage, size);
        response.content.forEach(reply => {
             reply.hasNestedReplies = false; 
             reply.showNested = false;
        }); 
      } else {
          throw new Error("Either tweetId or userId must be provided");
      }

      if (response.content.length === 0) {
        setHasMoreReplies(false);
      } else {
        setMainReplies(prev => currentPage === 0 ? response.content : [...prev, ...response.content]);
        setPage(currentPage + 1);
        setHasMoreReplies(currentPage + 1 < response.totalPages);
      }
    } catch (error) {
      console.error('Failed to load replies:', error);
      
    } finally {
      setIsLoading(false);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading, hasMoreReplies, tweetId, userId, size]);

  
  useEffect(() => {
    setMainReplies([]);
    setPage(0);
    setHasMoreReplies(true);
    loadReplies(0);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tweetId, userId]); // Rerun when tweetId or userId changes

  
  useEffect(() => {
    if (newReply) {
        if (!newReply.parentReplyId) {
            // Add to main replies if it's not a nested reply
            setMainReplies(prev => [newReply, ...prev]);
        } else {
            // manual refresh for nested 
        }
    }
  }, [newReply]);

  
  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const handleScroll = () => {
      const tolerance = 50; // Load slightly before reaching the absolute bottom
      const isNearBottom = container.scrollHeight - container.scrollTop <= container.clientHeight + tolerance;
      
      if (isNearBottom && !isLoading && hasMoreReplies) {
        loadReplies(page);
      }
    };

    container.addEventListener('scroll', handleScroll);
    return () => container.removeEventListener('scroll', handleScroll);
  }, [loadReplies, isLoading, hasMoreReplies, page]);

  const toggleNestedReplies = (replyId: number) => {
     setMainReplies(prevReplies => 
        prevReplies.map(reply => 
            reply.id === replyId ? { ...reply, showNested: !reply.showNested } : reply
        )
    );
  };

  return (
    <div className={styles.repliesContainer} ref={containerRef}>
      {mainReplies.map((mainReply) => (
        <div key={mainReply.id}>
          <SingleReply 
            reply={mainReply} 
            onOpenReplyPopup={onOpenReplyPopup}
            onToggleNested={mainReply.hasNestedReplies ? () => toggleNestedReplies(mainReply.id) : undefined}
            isNestedVisible={!!mainReply.showNested}
          />
          {mainReply.showNested && (
            <div className={styles.nestedReplies}>
                <NestedReply 
                    parentReplyId={mainReply.id} 
                    onOpenReplyPopup={onOpenReplyPopup} 
                />
            </div>
          )}
        </div>
      ))}
       {isLoading && <div>Loading more replies...</div>}
       {!hasMoreReplies && mainReplies.length > 0 && <div>No more replies</div>}
       {!hasMoreReplies && mainReplies.length === 0 && <div>Be the first to reply!</div>}
    </div>
  );
};

export default ReplyList;
