package mk.ukim.finki.library_vp.service.impl;

import mk.ukim.finki.library_vp.model.Book;
import mk.ukim.finki.library_vp.model.ReservationCart;
import mk.ukim.finki.library_vp.model.User;
import mk.ukim.finki.library_vp.model.exceptions.BookAlreadyInReservationCartException;
import mk.ukim.finki.library_vp.model.exceptions.BookNotFoundException;
import mk.ukim.finki.library_vp.model.exceptions.ReservationCartNotFoundException;
import mk.ukim.finki.library_vp.model.exceptions.UserNotFoundException;
import mk.ukim.finki.library_vp.repository.BookRepository;
import mk.ukim.finki.library_vp.repository.ReservationCartRepository;
import mk.ukim.finki.library_vp.repository.UserRepository;
import mk.ukim.finki.library_vp.service.ReservationCartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationCartServiceImpl implements ReservationCartService {

    private final ReservationCartRepository reservationCartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReservationCartServiceImpl(ReservationCartRepository reservationCartRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.reservationCartRepository = reservationCartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public ReservationCart findCartByUser(User user) {
        if (reservationCartRepository.findReservationCartByUser(user) != null) {
            return reservationCartRepository.findReservationCartByUser(user);
        }
        else
            return reservationCartRepository.save(new ReservationCart(user));
    }

    @Override
    public ReservationCart getActiveReservationCart(String username) {

        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        ReservationCart reservationCart = this.reservationCartRepository.
                findReservationCartByUser(user);


 //               .orElseThrow(() -> {
//                    ReservationCart cart = new ReservationCart(user);
//                    return this.reservationCartRepository.save(cart);
//                });
        return reservationCart;

//        return this.shoppingCartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.CREATED)
//                .orElseGet(() -> {
//                    User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
//                    ShoppingCart shoppingCart = new ShoppingCart(user);
//                    return this.shoppingCartRepository.save(shoppingCart);
//                });

    }

    @Override
    public ReservationCart addBookToReservationCart(String username, Long bookId) {
        ReservationCart reservationCart = this.getActiveReservationCart(username);
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        if(reservationCart.getBooks()
                .stream().filter(i -> i.getId().equals(bookId))
                .collect(Collectors.toList()).size() > 0)
            throw new BookAlreadyInReservationCartException(bookId, username);
        book.setStock(book.getStock() - 1);
        book = this.bookRepository.save(book);
        reservationCart.getBooks().add(book);
        return this.reservationCartRepository.save(reservationCart);
    }

    @Override
    public List<Book> listAllBooksInReservationCart(Long cartId) {
        if(!this.reservationCartRepository.findById(cartId).isPresent())
            throw new ReservationCartNotFoundException(cartId);
        return this.reservationCartRepository.findById(cartId).get().getBooks();

    }

    @Override
    public void checkout() {
        List<Book> books = this.bookRepository.findAll();

        for(int i = 0; i < books.size(); i++) {
            books.get(i).setStock(books.get(i).getStock() + 1);
        }

        books = this.bookRepository.saveAll(books);

    }


}
