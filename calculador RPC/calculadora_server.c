/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "calculadora.h"

float *
  suma_1_svc(entradas * argp, struct svc_req * rqstp) {
    static float result;

    result = argp -> num1 + argp -> num2;
    printf("Solicitud Recibida de Sumar %.2f y %.2f\n", argp -> num1, argp -> num2);
    printf("Enviando respuesta : %.2f\n", result);

    return &result;
  }

float *
  resta_1_svc(entradas * argp, struct svc_req * rqstp) {
    static float result;

    result = argp -> num1 - argp -> num2;
    printf("Solicitud Recibida de restar %.2f de %.2f\n", argp -> num2, argp -> num1);
    printf("Enviando respuesta : %.2f\n", result);

    return &result;
  }

float *
  mult_1_svc(entradas * argp, struct svc_req * rqstp) {
    static float result;

    result = argp -> num1 * argp -> num2;
    printf("Solicitud Recibida de multiplicar %.2f por %.2f\n", argp -> num1, argp -> num2);
    printf("Enviando respuesta : %.2f\n", result);

    return &result;
  }

float *
  div_1_svc(entradas * argp, struct svc_req * rqstp) {
    static float result;

    result = argp -> num1 / argp -> num2;
    printf("Solicitud Recibida de dividir %.2f entre %.2f\n", argp -> num1, argp -> num2);
    printf("Enviando respuesta : %.2f\n", result);

    return &result;
  }
