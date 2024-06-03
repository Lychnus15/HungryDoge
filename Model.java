package Doge;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {

	private Dimension d; //Membuat dimensi layar
    private final Font smallFont = new Font("Arial", Font.BOLD, 14); //Membuat score
    private boolean inGame = false; //Menandai bahwa game sedang tidak berjalan
    private boolean dying = false; //Menandai bahwa doge sedang mati
 
    private final int BLOCK_SIZE = 24; //Membuat ukuran block
    private final int N_BLOCKS = 15; //Membuat ukuran block
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; //Membuat ukuran layar
    private final int MAX_SLIMES = 12; //Membuat jumlah slime
    private final int DOGE_SPEED = 6; //Membuat kecepatan doge

    private int N_SLIMES = 6; //Membuat jumlah slime
    private int lives, score; //Membuat score dan nyawa
    private int[] dx, dy; //Membuat arah doge
    private int[] slime_x, slime_y, slime_dx, slime_dy, slimeSpeed; //Membuat arah slime

    private Image heart, slime; //Membuat gambar slime dan nyawa
    private Image up, down, left, right; //Mengatur gambar doge

    private int doge_x, doge_y, doged_x, doged_y; //Membuat posisi doge
    private int req_dx, req_dy; //Membuat arah doge

    //Membuat level atau rintangan yang akan dihadapi doge
    private final short levelData[] = {
    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 28,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
         0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20,  0,  0,  0,  0, 21,
        17, 24, 24, 24, 24, 16, 16, 24, 16, 20,  0,  0,  0,  0, 21,
        21,  0,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0,  0, 21,
        17, 18, 18, 22,  0, 17, 20,  0, 17, 16, 18, 18, 18, 18, 20,
        17, 24, 24, 28,  0, 25, 28,  0, 17, 16, 16, 16, 16, 16, 20,
        21,  0,  0,  0,  0,  0,  0,  0, 17, 16, 16, 24, 24, 16, 20,
        21,  0, 19, 22,  0, 19, 18, 18, 16, 16, 20,  0,  0, 17, 20,
        21,  0, 17, 20,  0, 25, 24, 24, 16, 16, 16, 18, 18, 16, 20,
        21,  0, 17, 20,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 20,
        25, 26, 24, 24, 26, 26, 26, 26, 24, 24, 24, 24, 24, 24, 28
    };

    // 19 = barier corner kiri atas
    // 22 = barier corner kanan atas
    // 25 = barier corner kiri bawah
    // 28 = barier corner kanan bawah

    // 21 = barier kiri kanan
    // 26 = barier bawah atas

    // 18 = barier atas
    // 17 = barier kiri
    // 20 = barier kanan
    // 24 = barier bawah

    // 0 = empty
    // 16 = score (white dot)

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8}; //Membuat kecepatan doge
    private final int maxSpeed = 6; //Membuat kecepatan maksimal doge

    private int currentSpeed = 3; //Membuat kecepatan doge saat ini
    private short[] screenData; //Membuat data layar
    private Timer timer; //Membuat timer

    //Membuat konstruktor Model
    public Model() {

        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }
    
    //Membuat method loadImages
    private void loadImages() {
    	down = new ImageIcon("D:/HungryDoge/Doge/images/dogeDOWN.gif").getImage();
    	up = new ImageIcon("D:/HungryDoge/Doge/images/dogeUP.gif").getImage();
    	left = new ImageIcon("D:/HungryDoge/Doge/images/dogeLEFT.gif").getImage();
    	right = new ImageIcon("D:/HungryDoge/Doge/images/dogeRIGHT.gif").getImage();
        slime = new ImageIcon("D:/HungryDoge/Doge/images/slime.gif").getImage();
        heart = new ImageIcon("D:/HungryDoge/Doge/images/heart.png").getImage();

    }

    //Membuat method initVariables
       private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];//Membuat data layar
        d = new Dimension(400, 400);//Membuat dimensi layar
        slime_x = new int[MAX_SLIMES];//Membuat posisi slime
        slime_dx = new int[MAX_SLIMES];//Membuat arah slime
        slime_y = new int[MAX_SLIMES];//Membuat posisi slime
        slime_dy = new int[MAX_SLIMES];//Membuat arah slime
        slimeSpeed = new int[MAX_SLIMES];//Membuat kecepatan slime
        dx = new int[4];//Membuat arah doge
        dy = new int[4];//Membuat arah doge
        
        timer = new Timer(40, this);
        timer.start();
    }

    //Membuat method untuk memulai game
    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            moveDoge();
            drawDoge(g2d);
            moveSlimes(g2d);
            checkMaze();
        }
    }

    //Membuat method tombol start
    private void showIntroScreen(Graphics2D g2d) {
 
    	String start = "Tekan spasi untuk memulai";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }

    //Membuat method untuk menambah score dan mengurangi nyawa
    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        } // 28 = jarak antar nyawa, 8 = jarak dari kiri, 1 = jarak dari bawah
    }

    //Jika semua slime sudah mati, maka akan menambah score dan mengurangi nyawa
    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        //Mengecek apakah semua slime sudah mati
        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        //Jika slime mati, maka akan menambah score
        if (finished) {

            score += 50;
            //Jika slime belum maksimal, maka akan menambah slime
            if (N_SLIMES < MAX_SLIMES) {
                N_SLIMES++;
            }
            //Jika kecepatan doge belum maksimal, maka akan menambah kecepatan doge
            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    //Membuat method jika mati
    private void death() {

    	lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveSlimes(Graphics2D g2d) {

        int pos;//Membuat posisi
        int count;//Membuat counter
        
        for (int i = 0; i < N_SLIMES; i++) {
            //Membuat posisi slime
            if (slime_x[i] % BLOCK_SIZE == 0 && slime_y[i] % BLOCK_SIZE == 0) {
                pos = slime_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (slime_y[i] / BLOCK_SIZE);

                count = 0;
                
                if ((screenData[pos] & 1) == 0 && slime_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }
                
                if ((screenData[pos] & 2) == 0 && slime_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && slime_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && slime_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }
                //Jika slime tidak bisa bergerak, maka akan bergerak mundur
                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        slime_dx[i] = 0;
                        slime_dy[i] = 0;
                    } else {
                        slime_dx[i] = -slime_dx[i];
                        slime_dy[i] = -slime_dy[i];
                    }

                }
                //Jika slime bisa bergerak, maka akan bergerak secara random
                else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    slime_dx[i] = dx[count];
                    slime_dy[i] = dy[count];
                }

            }

            //Membuat slime bergerak
            slime_x[i] = slime_x[i] + (slime_dx[i] * slimeSpeed[i]);
            slime_y[i] = slime_y[i] + (slime_dy[i] * slimeSpeed[i]);
            drawGhost(g2d, slime_x[i] + 1, slime_y[i] + 1);

            //Jika doge menyentuh slime, maka akan mati
            if (doge_x > (slime_x[i] - 12) && doge_x < (slime_x[i] + 12)
                    && doge_y > (slime_y[i] - 12) && doge_y < (slime_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(slime, x, y, this);
        }

    private void moveDoge() {

        int pos;
        short ch;

        if (doge_x % BLOCK_SIZE == 0 && doge_y % BLOCK_SIZE == 0) {
            pos = doge_x / BLOCK_SIZE + N_BLOCKS * (int) (doge_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
                if (score == 179) {
                    inGame = false; //finish game
                }
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    doged_x = req_dx;
                    doged_y = req_dy;
                }
            }

            // Check for standstill
            if ((doged_x == -1 && doged_y == 0 && (ch & 1) != 0) //left wall
                    || (doged_x == 1 && doged_y == 0 && (ch & 4) != 0) //right wall
                    || (doged_x == 0 && doged_y == -1 && (ch & 2) != 0) //top wall
                    || (doged_x == 0 && doged_y == 1 && (ch & 8) != 0))  //bottom wall
            {
                doged_x = 0;
                doged_y = 0;
            }
        } 
        doge_x = doge_x + DOGE_SPEED * doged_x;
        doge_y = doge_y + DOGE_SPEED * doged_y; 
    }

    private void drawDoge(Graphics2D g2d) {

        if (req_dx == -1) {
        	g2d.drawImage(left, doge_x + 1, doge_y + 1, this);
        } else if (req_dx == 1) {
        	g2d.drawImage(right, doge_x + 1, doge_y + 1, this);
        } else if (req_dy == -1) {
        	g2d.drawImage(up, doge_x + 1, doge_y + 1, this);
        } else {
        	g2d.drawImage(down, doge_x + 1, doge_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    //Membuat mekanisme game
    private void initGame() {

    	lives = 5;
        score = 0;
        initLevel();
        N_SLIMES = 6;
        currentSpeed = 3;
    }

    //Membuat mekanisme level
    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    //Membuat mekanisme lanjut level
    private void continueLevel() {

    	int dx = 1;
        int random;

        for (int i = 0; i < N_SLIMES; i++) {

            slime_y[i] = 4 * BLOCK_SIZE; //start position
            slime_x[i] = 4 * BLOCK_SIZE;
            slime_dy[i] = 0;
            slime_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            slimeSpeed[i] = validSpeeds[random];
        }

        doge_x = 7 * BLOCK_SIZE;  //start position
        doge_y = 11 * BLOCK_SIZE;
        doged_x = 0;	//reset direction move
        doged_y = 0;
        req_dx = 0;		// reset direction controls
        req_dy = 0;
        dying = false;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    //controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }

	
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
		
}

// kelas ini digunakan untuk mengatur keseluruhan game, seperti ukuran layar,
// ukuran blok, membuat lingkungan permainan, kontrol karakter, dan lain-lain.

// objek yang kami gunakan adalah objek dari kelas JPanel, 
// kelas Timer, dan kelas KeyAdapter.