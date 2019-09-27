merge(null, T, T) :- !.
merge(T, null, T) :- !.
merge(tree(K1, V1, P1, L1, R1), tree(K2, V2, P2, L2, R2), tree(K1, V1, P1, L1, RT)) :-
    P1 < P2, merge(R1, tree(K2, V2, P2, L2, R2), RT).
merge(tree(K1, V1, P1, L1, R1), tree(K2, V2, P2, L2, R2), tree(K2, V2, P2, LT, R2)) :-
    P1 >= P2, merge(tree(K1, V1, P1, L1, R1), L2, LT).

split(null, _, null, null, null) :- !.
split(tree(K, V, P, L, R), NK, tree(K, V, P, L, L1), M, Right) :-
    K < NK, split(R, NK, L1, M, Right), !.
split(tree(K, V, P, L, R), NK, Left, M, tree(K, V, P, R1, R)) :-
    K > NK, split(L, NK, Left, M, R1), !.
split(tree(K, V, P, L, R), K, L, tree(K, V, P, null, null), R) :- !.


map_put(null, K, V, tree(K, V, P, null, null)) :- rand_int(1000000000, P), !.
map_put(T, K, V, NT) :-
    split(T, K, L, _, R),
    rand_int(1000000000, P),
    merge(L, tree(K, V, P, null, null), M),
    merge(M, R, NT).

build([], T, T) :- !.
build([(K, V) | M], T, NT) :-
    map_put(T, K, V, A),
    build(M, A, NT).

tree_build(M, T) :-  build(M, null, T).

map_remove(T, K, NT) :-
    split(T, K , L, _, R),
    merge(L, R, NT).

map_get(T, K, V) :- split(T, K, _, tree(K, V, _, _, _), _).

map_replace(T, K, V, T) :-
    split(T, K, L, null, R), !.

map_replace(T, K, V, NT) :-
    split(T, K, Left, tree(K1, V1, P, L, R), Right),
    merge(Left, tree(K, V, P, L, R), M),
    merge(M, Right, NT).

findMax(tree(K, _, _, _, null), K) :-!.
findMax(tree(K, V, P, L, R), NK) :- findMax(R, NK).
map_floorKey(T, K, NK) :-
    split(T, K, L, tree(NK, _, _, _, _), R), !.
map_floorKey(T, K, NK) :-
    split(T, K, L, null, R),
    findMax(L, NK).




