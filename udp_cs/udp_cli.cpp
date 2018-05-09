#include<stdio.h>
#include<unistd.h>
#include<stdlib.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<string.h>

#define MAX_BUF_SIZE 1024
#define PORT 8888

int main() 
{
   int sockfd, addrlen, n;
   char buffer[MAX_BUF_SIZE];
   struct sockaddr_in addr;
   sockfd = socket(AF_INET, SOCK_DGRAM, 0);
   if (sockfd < 0)
   {
      fprintf(stderr, "socket falied\n");
      exit(EXIT_FAILURE);
   }
   addrlen = sizeof(struct sockaddr_in);
   bzero(&addr, addrlen);
   addr.sin_family = AF_INET;
   addr.sin_port = htons(PORT);
   addr.sin_addr.s_addr = htonl(INADDR_ANY);

   //客户端也绑定一个端口玩一玩❤️
   int cli_addrlen;
   struct sockaddr_in cli_addr;
   cli_addrlen = sizeof(struct sockaddr_in);
   bzero(&cli_addr, cli_addrlen);
   cli_addr.sin_family = AF_INET;
   cli_addr.sin_port = htons(12345);
   cli_addr.sin_addr.s_addr = htonl(INADDR_ANY);

   if (bind(sockfd, (struct sockaddr*)(&cli_addr), cli_addrlen) < 0)
   {
      fprintf(stderr, "bind fail\n");
      exit(EXIT_FAILURE);
   }
     
   puts("socket success");
   while(1)
   {
       bzero(buffer, MAX_BUF_SIZE);
       fgets(buffer, MAX_BUF_SIZE, stdin);
       sendto(sockfd, buffer, strlen(buffer), 0, (struct sockaddr *)(&addr), addrlen);
       printf("client send msg is %s\n", buffer);
       n = recvfrom(sockfd, buffer, strlen(buffer), 0, (struct sockaddr *)(&addr), (socklen_t *)&addrlen);
       fprintf(stdout, "clinet Receive message from server is %s\n", buffer);

       fflush(stdin);
   }
   close(sockfd);
   exit(0);
   return 0;
}
