#ifndef nback_h
#define nback_h

#define CONTENT_MAX_SIZE 100

struct nback_type {
    int content[CONTENT_MAX_SIZE];
    int size;
};

typedef struct nback_type *Nback;

Nback create(int size, int combinations, int matchPercentage, int nback);
int getIndexOf(Nback s, int index);

#endif
