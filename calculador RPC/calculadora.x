struct entradas{
  float num1;
  float num2;
  char operador;
};

program CALCULADORA_PROG{
 version CALCULADORA_VER{
 float SUMA(entradas)=1;
 float RESTA(entradas)=2;
 float MULT(entradas)=3;
 float DIV(entradas)=4;
 }=1;
}=0x2fffffff;
