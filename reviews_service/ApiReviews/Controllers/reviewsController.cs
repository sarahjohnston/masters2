using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using ApiReviews.Models;
using System.Web.Http.Cors;

namespace ApiReviews.Controllers
{
    [EnableCors(origins: "*", headers: "*", methods: "*")]
    public class reviewsController : ApiController
    {
        private ReviewsModel db = new ReviewsModel();

        // GET: api/reviews
        public IQueryable<review> Getreviews()
        {
            return db.reviews;
        }

        // GET: api/reviews/5
        [ResponseType(typeof(review))]
        public IHttpActionResult Getreview(int id)
        {
            review review = db.reviews.Find(id);
            if (review == null)
            {
                return NotFound();
            }

            return Ok(review);
        }

        // GET: api/reviews/place/5
        [Route("api/reviews/place/{id:int}")]
        public IEnumerable<review> GetReviewsByPlace(int id)
        {
            return db.reviews.Where(
                r => r.PlaceId == id).OrderByDescending(r => r.Date);
        }

        // PUT: api/reviews/5
         [ResponseType(typeof(void))]
         public IHttpActionResult Putreview(int id, review review)
         {
             if (!ModelState.IsValid)
             {
                return BadRequest(ModelState);
             }

             if (id != review.Id)
             {
                return BadRequest();

             }

             review SelectedReview = db.reviews.Find(id);
             place place = db.places.Find(review.PlaceId);

             int NewRating = (int)review.Rating;
             int OldRating = (int)SelectedReview.Rating;
             place.TotalRatings -= OldRating;
             place.TotalRatings += NewRating;
             SelectedReview.Headline = review.Headline;
             SelectedReview.Date = review.Date;
             SelectedReview.Rating = review.Rating;
             SelectedReview.ReviewText = review.ReviewText;
    
             try
             {
                 db.SaveChanges();
             }
             catch (DbUpdateConcurrencyException)
             {
                 if (!reviewExists(id))
                 {
                     return NotFound();
                 }
                 else
                 {
                    throw;
                 }
             }

             return StatusCode(HttpStatusCode.NoContent);
         }


        // POST: api/reviews
        [ResponseType(typeof(review))]
        public IHttpActionResult Postreview(review review)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            //increment place rating and number of reviews
            int PlaceId = (int)review.PlaceId; 
            place place = db.places.Find(PlaceId);
            if (place == null)
            {
                return BadRequest("Can't add review as no such place in database.");
            }
            else
            {
                //update place rating counts
                place.NumberReviews += 1;
                place.TotalRatings += (int)review.Rating;
                db.Entry(place).State = EntityState.Modified;

                db.reviews.Add(review);
                db.SaveChanges();

                return CreatedAtRoute("DefaultApi", new { id = review.Id }, review);
            }

            
        }

        // DELETE: api/reviews/5
        [ResponseType(typeof(review))]
        public IHttpActionResult Deletereview(int id)
        {
            review review = db.reviews.Find(id);
            if (review == null)
            {
                return NotFound();
            }

            int? PlaceId = review.PlaceId;
            place currentPlace = db.places.Find(PlaceId);
            if (currentPlace == null)
            {
                return NotFound();
            }
            currentPlace.NumberReviews -= 1;
            currentPlace.TotalRatings -= (int)review.Rating;

            db.Entry(currentPlace).State = EntityState.Modified;
            db.reviews.Remove(review);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!placeExists(currentPlace.Id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }
            
            db.SaveChanges();

            return Ok(review);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool reviewExists(int id)
        {
            return db.reviews.Count(e => e.Id == id) > 0;
        }

        private bool placeExists(int id)
        {
            return db.places.Count(e => e.Id == id) > 0;
        }
    }
}