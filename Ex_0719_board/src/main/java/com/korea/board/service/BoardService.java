package com.korea.board.service;

import java.util.HashMap;
import java.util.List;

import com.korea.board.vo.BoardVO;

public interface BoardService {

	public List<BoardVO> selectList(HashMap<String, Integer> map);
	
	public int getRowTotal();
	
	public BoardVO selectOne(int idx);
	
	public int update_readhit(int idx);
	
	public int insert(BoardVO vo);
	
	public int del_update(BoardVO vo);

	public int board_update_step(BoardVO vo);

	public int reply(BoardVO vo);

}
