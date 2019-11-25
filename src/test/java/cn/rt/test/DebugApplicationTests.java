package cn.rt.test;

public class DebugApplicationTests {

	private static final int queue = 8;
	private static int count = 0;

	public static void main(String[] args) {
		short[][] chess = new short[queue][queue];
		// 初始化棋盘
		initChess(chess);
		putQueenAtRow(chess,0);
	}

	@SuppressWarnings("unused")
	private static void putQueenAtRow(short[][] chess, int row) {
		// 如果第8行放入了最后一个皇后，递归终止
		if (row == queue) {
			count++;
			System.out.println("第" + count + "解法");
			for (int i = 0; i < queue; i++) {
				for (int j = 0; j < queue; j++) {
					System.out.print(chess[i][j] + " ");
				}
				System.out.println();
			}
			return;
		}

		// 克隆一个temp的chess
		short[][] chessTemp = chess.clone();

		/**
		 * 向这一行的每一个位置尝试排放皇后 然后检测状态，如果安全则继续执行递归函数摆放下一行皇后
		 */
		for (int i = 0; i < queue; i++) {
			// 摆放这一行的皇后，之前要清掉所有这一行摆放的记录，防止污染棋盘
			for (int j = 0; j < queue; j++)
				chessTemp[row][j] = 0;
				chessTemp[row][i] = 1;

			if (isSafety(chessTemp, row, i)) {
				putQueenAtRow(chessTemp, row + 1);
			}
		}

	}

	private static boolean isSafety(short[][] chess, int row, int col) {
		// 判断中上、左上、右上是否安全
		int step = 1;
		while (row - step >= 0) {
			if (chess[row - step][col] == 1) // 中上
				return false;
			if (col - step >= 0 && chess[row - step][col - step] == 1) // 左上
				return false;
			if (col + step < queue && chess[row - step][col + step] == 1) // 右上
				return false;

			step++;
		}
		return true;
	}

	static void initChess(short[][] chess) {
		for (int i = 0; i < queue; i++) {
			for (int j = 0; j < queue; j++) {
				chess[i][j] = 0;
			}
		}
	}

}
