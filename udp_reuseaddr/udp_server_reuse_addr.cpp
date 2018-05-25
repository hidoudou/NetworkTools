/*
测试udp编程中的端口绑定问题。
两个udp 套接字绑定同一个本地端口，默认情况下是不行的。
如果实在需要两个套接字绑定到同一个【ip，port】上，需要额外设置套接字选项

本程序在Mac机器上测试通过。
2018/5/25：在mac平台上，使用SO_REUSEADDR不能实现端口复用，需要使用SO_REUSEPORT
至于在其他平台上应该使用哪种选项，待进一步验证，不过目前可以肯定的是SO_REUSEPORT/ADDR 在不同平台的实现是不同的。
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <pthread.h>

int sockfd_one, sockfd_two;

void *recv_one(void* arg) {
    printf("+++++++++++++++++++++++++recv_one++++++++++++++++++\n");

    while(true) {
        int recv_len;
        char recv_buf[1024] = {0};
        struct sockaddr_in client_addr;
        char cli_ip[INET_ADDRSTRLEN] = {0};
        socklen_t cliaddr_len = sizeof(client_addr);

        recv_len = recvfrom(sockfd_one, recv_buf, sizeof(recv_buf), 0, (struct sockaddr*)&client_addr, &cliaddr_len);
        inet_ntop(AF_INET, &client_addr.sin_addr, cli_ip, INET_ADDRSTRLEN);
        printf("\nip:%s, port:%d\n", cli_ip, ntohs(client_addr.sin_port));
        printf("sockfd_one===========data(%d):%s\n", recv_len, recv_buf);
    }
    return NULL;
}

void* recv_two(void* arg) {
    printf("++++++++++++++++++++++++redv_two++++++++++++++++++\n");

    while(true) {
        int recv_len;
        char recv_buf[1024] = {0};
        struct sockaddr_in client_addr;
        char cli_ip[INET_ADDRSTRLEN] = {0};
        socklen_t cliaddr_len = sizeof(client_addr);

        recv_len = recvfrom(
            sockfd_two, recv_buf, sizeof(recv_buf), 0, (struct sockaddr*)&client_addr, &cliaddr_len);
        inet_ntop(AF_INET, &client_addr.sin_addr, cli_ip, INET_ADDRSTRLEN);
        printf("\nip:%s, port:%d\n", cli_ip, ntohs(client_addr.sin_port));
        printf("sockfd_two @@@@@@@@@@@@@@@@@ data(%d):%s\n", recv_len, recv_buf);
    }
    return NULL;
}

int main(int argc, char* argv[]) {
    int err_log;

    sockfd_one = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd_one < 0) {
        perror("sockfd_one");
        exit(-1);
    }

    struct sockaddr_in my_addr;
    bzero(&my_addr, sizeof(struct sockaddr_in));
    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(5050);
    my_addr.sin_addr.s_addr = htonl(INADDR_ANY);

    int opt = 1;
    setsockopt(sockfd_one, SOL_SOCKET, SO_REUSEPORT, (const void*)&opt, sizeof(opt));

    err_log = bind(sockfd_one, 
                    (struct sockaddr*)&my_addr, sizeof(my_addr));
    if(err_log != 0) {
        perror("bind sockfd_one");
        close(sockfd_one);
        exit(-1);
    }

    pthread_t tid_one;
    pthread_create(&tid_one, NULL, recv_one, (void*)sockfd_one);

    sockfd_two = socket(AF_INET, SOCK_DGRAM, 0);
    if(sockfd_two < 0) {
        perror("sockfd_two");
        exit(-1);
    }

    opt = 1;
    setsockopt(sockfd_two, SOL_SOCKET, SO_REUSEPORT,
                (const void*)&opt, sizeof(opt));
    
    err_log = bind(sockfd_two, (struct sockaddr*)&my_addr, sizeof(my_addr));
    if (err_log != 0) {
        perror("bind sockfd_two");
        close(sockfd_two);
        exit(-1);
    }

    pthread_t tid_two;
    pthread_create(&tid_two, NULL, recv_two, (void*)sockfd_two);

    while(true) {
        ;
    }

    close(sockfd_one);
    close(sockfd_two);

    return 0;
}